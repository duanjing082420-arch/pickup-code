package cn.pickup.launcher;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 本地包裹存储：用 SharedPreferences 保存手动录入的取件码，
 * 重启 App 不丢失，不需要任何联网或敏感权限。
 */
final class PackageStore {
    static final class Item {
        String carrier = "";   // 快递公司 / 平台，如：中通、菜鸟驿站
        String code = "";      // 取件码，如：3-1-2040
        String station = "";   // 驿站位置（可选）
        long createdAt;        // 录入时间
    }

    private static final String PREFS_NAME = "pickup_packages";
    private static final String KEY_ITEMS = "items";
    private static final String KEY_HANDLED_CLIP = "handled_clip";

    private PackageStore() {
    }

    /** 新增一条（按取件码去重），供短信 / 通知自动入库使用。 */
    static boolean addIfAbsent(Context context, String carrier, String code, String station) {
        if (code == null || code.trim().isEmpty()) {
            return false;
        }
        String trimmed = code.trim();
        List<Item> items = load(context);
        for (Item item : items) {
            if (trimmed.equals(item.code)) {
                return false;
            }
        }
        Item item = new Item();
        item.carrier = carrier == null ? "" : carrier;
        item.code = trimmed;
        item.station = station == null ? "" : station;
        item.createdAt = System.currentTimeMillis();
        items.add(0, item);
        save(context, items);
        return true;
    }

    /** 剪贴板内容是否已处理过（避免同一段文字反复弹窗）。 */
    static boolean isClipHandled(Context context, String value) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return value.equals(prefs.getString(KEY_HANDLED_CLIP, ""));
    }

    static void markClipHandled(Context context, String value) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_HANDLED_CLIP, value).apply();
    }

    static List<Item> load(Context context) {
        List<Item> items = new ArrayList<>();
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String raw = prefs.getString(KEY_ITEMS, "[]");
        try {
            JSONArray array = new JSONArray(raw);
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                Item item = new Item();
                item.carrier = object.optString("carrier", "");
                item.code = object.optString("code", "");
                item.station = object.optString("station", "");
                item.createdAt = object.optLong("createdAt", 0L);
                items.add(item);
            }
        } catch (Exception ignored) {
            // 数据损坏时当作空列表处理，不影响使用
        }
        return items;
    }

    static void save(Context context, List<Item> items) {
        JSONArray array = new JSONArray();
        try {
            for (Item item : items) {
                JSONObject object = new JSONObject();
                object.put("carrier", item.carrier);
                object.put("code", item.code);
                object.put("station", item.station);
                object.put("createdAt", item.createdAt);
                array.put(object);
            }
        } catch (Exception ignored) {
        }
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_ITEMS, array.toString()).apply();
    }
}
