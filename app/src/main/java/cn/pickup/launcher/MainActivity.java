package cn.pickup.launcher;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public final class MainActivity extends Activity {
    static final String ACTION_OPEN = "cn.pickup.launcher.OPEN";
    static final String EXTRA_DESTINATION = "destination";

    // ===== 水蓝清凉主题 =====
    private static final int PAGE_BACKGROUND = Color.rgb(240, 246, 249);
    private static final int CARD_WHITE = Color.WHITE;
    private static final int TEXT_PRIMARY = Color.rgb(27, 43, 51);
    private static final int TEXT_SECONDARY = Color.rgb(116, 133, 142);
    private static final int LINE_COLOR = Color.rgb(223, 233, 238);
    private static final int ACCENT_WATER = Color.rgb(42, 122, 158);
    private static final int NAV_IDLE = Color.rgb(154, 166, 157);

    // ===== 平台品牌色（渐变起止） =====
    private static final int[] CAINIAO_COLORS = {Color.rgb(63, 179, 212), Color.rgb(31, 122, 158)};
    private static final int[] TAOBAO_COLORS = {Color.rgb(255, 144, 70), Color.rgb(232, 115, 44)};
    private static final int[] PDD_COLORS = {Color.rgb(232, 80, 106), Color.rgb(214, 58, 82)};
    private static final int[] JD_COLORS = {Color.rgb(224, 74, 50), Color.rgb(196, 52, 31)};
    private static final int[] XHS_COLORS = {Color.rgb(232, 98, 156), Color.rgb(216, 67, 124)};
    private static final int[] DOUYIN_COLORS = {Color.rgb(43, 43, 51), Color.rgb(15, 15, 20)};

    // 当前选中的身份码平台，默认淘宝
    private Destination selectedIdentity = Destination.TAOBAO;

    private LinearLayout identityCard;
    private TextView identityTitle;
    private TextView identitySubtitle;
    private LinearLayout segmentRow;
    private LinearLayout pendingTabButton;
    private LinearLayout codeTabButton;
    private ScrollView pendingPage;
    private ScrollView codePage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Destination destination = destinationFromIntent();
        if (destination != null) {
            DeepLinkLauncher.open(this, destination);
            finish();
            return;
        }

        getWindow().setStatusBarColor(PAGE_BACKGROUND);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(buildContent());
    }

    private Destination destinationFromIntent() {
        String action = getIntent().getAction();
        if (ACTION_OPEN.equals(action)) {
            return Destination.fromKey(getIntent().getStringExtra(EXTRA_DESTINATION));
        }
        if ("cn.pickup.launcher.OPEN_CAINIAO".equals(action)) {
            return Destination.CAINIAO;
        }
        if ("cn.pickup.launcher.OPEN_TAOBAO".equals(action)) {
            return Destination.TAOBAO;
        }
        if ("cn.pickup.launcher.OPEN_PINDUODUO".equals(action)) {
            return Destination.PINDUODUO;
        }
        if ("cn.pickup.launcher.OPEN_TAOBAO_PENDING".equals(action)) {
            return Destination.TAOBAO_PENDING;
        }
        if ("cn.pickup.launcher.OPEN_PINDUODUO_PENDING".equals(action)) {
            return Destination.PINDUODUO_PENDING;
        }
        if ("cn.pickup.launcher.OPEN_JD".equals(action)) {
            return Destination.JD;
        }
        if ("cn.pickup.launcher.OPEN_XHS".equals(action)) {
            return Destination.XHS;
        }
        return null;
    }

    // =====================================================================
    // 页面骨架：内容区（两个可切换页面） + 底部导航
    // =====================================================================

    private View buildContent() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(PAGE_BACKGROUND);

        FrameLayout content = new FrameLayout(this);
        pendingPage = buildPendingPage();
        codePage = buildCodePage();
        codePage.setVisibility(View.GONE);
        content.addView(pendingPage, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        ));
        content.addView(codePage, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        ));
        root.addView(content, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
        ));

        root.addView(buildNavBar(), new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        return root;
    }

    private View buildNavBar() {
        LinearLayout bar = new LinearLayout(this);
        bar.setOrientation(LinearLayout.HORIZONTAL);
        bar.setBackgroundColor(CARD_WHITE);
        bar.setPadding(dp(24), dp(9), dp(24), dp(9) + getNavBarPadding());

        pendingTabButton = navItem("📦", "待取快递", true);
        pendingTabButton.setOnClickListener(v -> switchTab(true));
        codeTabButton = navItem("🔢", "取件码", false);
        codeTabButton.setOnClickListener(v -> switchTab(false));

        bar.addView(pendingTabButton, new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        bar.addView(codeTabButton, new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        return bar;
    }

    private LinearLayout navItem(String icon, String label, boolean active) {
        LinearLayout item = new LinearLayout(this);
        item.setOrientation(LinearLayout.VERTICAL);
        item.setGravity(Gravity.CENTER);
        item.setClickable(true);
        item.setFocusable(true);

        TextView iconView = text(icon, 20, active ? ACCENT_WATER : NAV_IDLE, Typeface.NORMAL);
        iconView.setGravity(Gravity.CENTER);
        item.addView(iconView);

        TextView labelView = text(label, 11, active ? ACCENT_WATER : NAV_IDLE, Typeface.BOLD);
        labelView.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams labelParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        labelParams.topMargin = dp(3);
        labelView.setLayoutParams(labelParams);
        item.addView(labelView);
        return item;
    }

    private void switchTab(boolean pending) {
        pendingPage.setVisibility(pending ? View.VISIBLE : View.GONE);
        codePage.setVisibility(pending ? View.GONE : View.VISIBLE);
        restyleNavItem(pendingTabButton, pending);
        restyleNavItem(codeTabButton, !pending);
    }

    private void restyleNavItem(LinearLayout item, boolean active) {
        int color = active ? ACCENT_WATER : NAV_IDLE;
        for (int i = 0; i < item.getChildCount(); i++) {
            View child = item.getChildAt(i);
            if (child instanceof TextView) {
                ((TextView) child).setTextColor(color);
            }
        }
    }

    private int getNavBarPadding() {
        int resourceId = getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return getResources().getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    // =====================================================================
    // 页面一：待取快递
    // =====================================================================

    private ScrollView buildPendingPage() {
        ScrollView scrollView = new ScrollView(this);
        scrollView.setFillViewport(true);
        scrollView.setBackgroundColor(PAGE_BACKGROUND);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(18), dp(14), dp(18), dp(20));
        scrollView.addView(root, new ScrollView.LayoutParams(
                ScrollView.LayoutParams.MATCH_PARENT,
                ScrollView.LayoutParams.WRAP_CONTENT
        ));

        // ---- 顶部问候 ----
        root.addView(buildHero("你好 👋", "先看有没有包裹，再出发去驿站"));

        // ---- 五个待取入口 ----
        LinearLayout grid = new LinearLayout(this);
        grid.setOrientation(LinearLayout.HORIZONTAL);
        grid.setWeightSum(5f);
        grid.setPadding(0, dp(4), 0, dp(4));
        grid.addView(quickEntry(Destination.TAOBAO_PENDING, "淘", "淘宝待取", TAOBAO_COLORS), horizontalWeightParams(0));
        grid.addView(quickEntry(Destination.PINDUODUO_PENDING, "拼", "拼多多待取", PDD_COLORS), horizontalWeightParams(dp(5)));
        grid.addView(quickEntry(Destination.JD, "京", "京东待取", JD_COLORS), horizontalWeightParams(dp(5)));
        grid.addView(quickEntry(Destination.XHS, "书", "小红书待取", XHS_COLORS), horizontalWeightParams(dp(5)));
        grid.addView(quickEntry(Destination.DOUYIN, "抖", "抖音待取", DOUYIN_COLORS), horizontalWeightParams(dp(5)));
        LinearLayout.LayoutParams gridParams = verticalParams(dp(12));
        grid.setLayoutParams(gridParams);
        root.addView(grid);

        // ---- 分区标题 ----
        root.addView(sectionTitle("待取快递", dp(10)));

        // ---- 提示条 ----
        root.addView(hintCard("📦", "点击下方卡片打开对应平台的待取列表，确认有包裹再去驿站。"));

        // ---- 待取列表卡片 ----
        root.addView(pendingCard(Destination.TAOBAO_PENDING, "淘宝待取快递", "打开淘宝末端驿站待取列表", TAOBAO_COLORS));
        root.addView(pendingCard(Destination.PINDUODUO_PENDING, "拼多多待取快递", "打开拼多多包裹待取列表", PDD_COLORS));
        root.addView(pendingCard(Destination.JD, "京东待取快递", "打开京东订单列表", JD_COLORS));
        root.addView(pendingCard(Destination.XHS, "小红书待取快递", "打开小红书订单列表", XHS_COLORS));
        root.addView(pendingCard(Destination.DOUYIN, "抖音待取快递", "打开抖音商城订单，查看包裹状态", DOUYIN_COLORS));

        // ---- 添加到桌面 ----
        root.addView(sectionTitle("添加到桌面", dp(8)));

        root.addView(hintCard("📌", "添加后不必进入本应用，桌面点击一次即可跳转。"));

        LinearLayout pinRow1 = new LinearLayout(this);
        pinRow1.setOrientation(LinearLayout.HORIZONTAL);
        pinRow1.setWeightSum(4f);
        pinRow1.setLayoutParams(verticalParams(dp(12)));
        addPinButton(pinRow1, Destination.TAOBAO, "淘宝码");
        addPinButton(pinRow1, Destination.PINDUODUO, "拼多多码");
        addPinButton(pinRow1, Destination.JD, "京东待取");
        addPinButton(pinRow1, Destination.XHS, "小红书待取");
        root.addView(pinRow1);

        LinearLayout pinRow2 = new LinearLayout(this);
        pinRow2.setOrientation(LinearLayout.HORIZONTAL);
        pinRow2.setWeightSum(4f);
        LinearLayout.LayoutParams row2Params = verticalParams(dp(8));
        pinRow2.setLayoutParams(row2Params);
        addPinButton(pinRow2, Destination.CAINIAO, "菜鸟码");
        addPinButton(pinRow2, Destination.TAOBAO_PENDING, "淘宝待取");
        addPinButton(pinRow2, Destination.PINDUODUO_PENDING, "拼多多待取");
        addPinButton(pinRow2, Destination.DOUYIN, "抖音待取");
        root.addView(pinRow2);

        return scrollView;
    }

    private View buildHero(String title, String subtitle) {
        LinearLayout hero = new LinearLayout(this);
        hero.setOrientation(LinearLayout.HORIZONTAL);
        hero.setGravity(Gravity.CENTER_VERTICAL);
        hero.setLayoutParams(verticalParams(0));

        LinearLayout titles = new LinearLayout(this);
        titles.setOrientation(LinearLayout.VERTICAL);

        TextView titleView = text(title, 24, TEXT_PRIMARY, Typeface.BOLD);
        titles.addView(titleView);

        TextView subtitleView = text(subtitle, 13, TEXT_SECONDARY, Typeface.NORMAL);
        LinearLayout.LayoutParams subtitleParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        subtitleParams.topMargin = dp(4);
        subtitleView.setLayoutParams(subtitleParams);
        titles.addView(subtitleView);

        hero.addView(titles, new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        TextView avatar = text("取", 16, ACCENT_WATER, Typeface.BOLD);
        avatar.setGravity(Gravity.CENTER);
        avatar.setBackground(rounded(Color.rgb(227, 241, 247), 14));
        hero.addView(avatar, new LinearLayout.LayoutParams(dp(42), dp(42)));

        return hero;
    }

    private View quickEntry(Destination destination, String mark, String label, int[] colors) {
        LinearLayout item = new LinearLayout(this);
        item.setOrientation(LinearLayout.VERTICAL);
        item.setGravity(Gravity.CENTER_HORIZONTAL);
        item.setClickable(true);
        item.setFocusable(true);
        item.setContentDescription("打开" + destination.title);
        item.setOnClickListener(v -> DeepLinkLauncher.open(this, destination));

        TextView icon = text(mark, 18, Color.WHITE, Typeface.BOLD);
        icon.setGravity(Gravity.CENTER);
        icon.setBackground(gradient(colors, 17));
        item.addView(icon, new LinearLayout.LayoutParams(dp(50), dp(50)));

        TextView labelView = text(label, 11, TEXT_SECONDARY, Typeface.NORMAL);
        labelView.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams labelParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        labelParams.topMargin = dp(7);
        labelView.setLayoutParams(labelParams);
        item.addView(labelView);
        return item;
    }

    private View pendingCard(
            Destination destination,
            String title,
            String subtitle,
            int[] colors
    ) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.HORIZONTAL);
        card.setGravity(Gravity.CENTER_VERTICAL);
        card.setPadding(dp(16), dp(15), dp(14), dp(15));
        card.setMinimumHeight(dp(78));
        card.setBackground(rounded(CARD_WHITE, 18));
        card.setClickable(true);
        card.setFocusable(true);
        card.setContentDescription("打开" + destination.title);
        card.setOnClickListener(v -> DeepLinkLauncher.open(this, destination));

        LinearLayout.LayoutParams cardParams = verticalParams(dp(12));
        card.setLayoutParams(cardParams);

        TextView mark = text(destination.mark, 16, Color.WHITE, Typeface.BOLD);
        mark.setGravity(Gravity.CENTER);
        mark.setBackground(gradient(colors, 12));
        card.addView(mark, new LinearLayout.LayoutParams(dp(42), dp(42)));

        LinearLayout labels = new LinearLayout(this);
        labels.setOrientation(LinearLayout.VERTICAL);
        labels.setPadding(dp(14), 0, dp(10), 0);

        TextView titleView = text(title, 16, TEXT_PRIMARY, Typeface.BOLD);
        labels.addView(titleView);

        TextView subtitleView = text(subtitle, 12, TEXT_SECONDARY, Typeface.NORMAL);
        LinearLayout.LayoutParams subtitleParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        subtitleParams.topMargin = dp(3);
        subtitleView.setLayoutParams(subtitleParams);
        labels.addView(subtitleView);

        card.addView(labels, new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        TextView arrow = text("›", 26, colors[1], Typeface.NORMAL);
        arrow.setGravity(Gravity.CENTER);
        card.addView(arrow, new LinearLayout.LayoutParams(dp(28), dp(42)));

        return card;
    }

    // =====================================================================
    // 页面二：取件码
    // =====================================================================

    private ScrollView buildCodePage() {
        ScrollView scrollView = new ScrollView(this);
        scrollView.setFillViewport(true);
        scrollView.setBackgroundColor(PAGE_BACKGROUND);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(18), dp(14), dp(18), dp(20));
        scrollView.addView(root, new ScrollView.LayoutParams(
                ScrollView.LayoutParams.MATCH_PARENT,
                ScrollView.LayoutParams.WRAP_CONTENT
        ));

        root.addView(buildHero("取件码", "到驿站亮出身份码，扫码即取"));

        // ---- 平台切换器：菜鸟 / 淘宝 / 拼多多 ----
        segmentRow = new LinearLayout(this);
        segmentRow.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams segmentParams = verticalParams(dp(14));
        segmentRow.setLayoutParams(segmentParams);

        segmentRow.addView(segButton(Destination.CAINIAO, "菜", "菜鸟", CAINIAO_COLORS), horizontalWeightParams(0));
        segmentRow.addView(segButton(Destination.TAOBAO, "淘", "淘宝", TAOBAO_COLORS), horizontalWeightParams(dp(8)));
        segmentRow.addView(segButton(Destination.PINDUODUO, "拼", "拼多多", PDD_COLORS), horizontalWeightParams(dp(8)));
        root.addView(segmentRow);

        // ---- 身份码大卡 ----
        identityCard = new LinearLayout(this);
        identityCard.setOrientation(LinearLayout.VERTICAL);
        identityCard.setGravity(Gravity.CENTER_HORIZONTAL);
        identityCard.setPadding(dp(22), dp(20), dp(22), dp(20));
        identityCard.setClickable(true);
        identityCard.setFocusable(true);
        identityCard.setContentDescription("打开当前身份码页面");
        identityCard.setOnClickListener(v -> DeepLinkLauncher.open(this, selectedIdentity));

        LinearLayout.LayoutParams cardParams = verticalParams(dp(16));
        cardParams.bottomMargin = dp(18);
        identityCard.setLayoutParams(cardParams);

        LinearLayout caption = new LinearLayout(this);
        caption.setOrientation(LinearLayout.HORIZONTAL);

        identityTitle = text("", 14, Color.WHITE, Typeface.BOLD);
        caption.addView(identityTitle, new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        TextView refresh = text("点击打开 ›", 12, Color.WHITE, Typeface.NORMAL);
        caption.addView(refresh);
        identityCard.addView(caption);

        TextView markBig = text("🎫", 52, Color.WHITE, Typeface.NORMAL);
        markBig.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams markParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        markParams.topMargin = dp(16);
        markBig.setLayoutParams(markParams);
        identityCard.addView(markBig);

        TextView identityNum = text("身份码", 22, Color.WHITE, Typeface.BOLD);
        identityNum.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams numParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        numParams.topMargin = dp(10);
        identityNum.setLayoutParams(numParams);
        identityCard.addView(identityNum);

        identitySubtitle = text("", 12, Color.WHITE, Typeface.NORMAL);
        identitySubtitle.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams subParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        subParams.topMargin = dp(6);
        identitySubtitle.setLayoutParams(subParams);
        identityCard.addView(identitySubtitle);

        TextView tip = text("🛡️ 本码仅用于取件核验，请勿截图外传", 11, Color.WHITE, Typeface.NORMAL);
        tip.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams tipParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        tipParams.topMargin = dp(14);
        tip.setLayoutParams(tipParams);
        identityCard.addView(tip);

        root.addView(identityCard);
        applyIdentitySelection();

        // ---- 我的取件码列表 ----
        root.addView(sectionTitle("我的取件码", 0));

        root.addView(identityListItem(Destination.CAINIAO, "菜鸟驿站身份码", "出示给驿站工作人员", CAINIAO_COLORS));
        root.addView(identityListItem(Destination.TAOBAO, "淘宝驿站身份码", "末端取件平台身份码", TAOBAO_COLORS));
        root.addView(identityListItem(Destination.PINDUODUO, "拼多多身份码", "多多买菜取件码", PDD_COLORS));

        return scrollView;
    }

    private View segButton(Destination destination, String mark, String label, int[] colors) {
        LinearLayout item = new LinearLayout(this);
        item.setOrientation(LinearLayout.HORIZONTAL);
        item.setGravity(Gravity.CENTER);
        item.setClickable(true);
        item.setFocusable(true);
        item.setContentDescription("切换到" + destination.title);
        item.setTag(destination);
        item.setOnClickListener(v -> {
            selectedIdentity = destination;
            applyIdentitySelection();
        });

        TextView markView = text(mark, 12, Color.WHITE, Typeface.BOLD);
        markView.setGravity(Gravity.CENTER);
        markView.setBackground(gradient(colors, 8));
        item.addView(markView, new LinearLayout.LayoutParams(dp(24), dp(24)));

        TextView labelView = text(label, 13, TEXT_PRIMARY, Typeface.BOLD);
        LinearLayout.LayoutParams labelParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        labelParams.leftMargin = dp(8);
        labelView.setLayoutParams(labelParams);
        item.addView(labelView);
        return item;
    }

    private void applyIdentitySelection() {
        String title;
        String subtitle;
        int[] colors;
        switch (selectedIdentity) {
            case CAINIAO:
                title = "菜鸟驿站身份码";
                subtitle = "向驿站工作人员出示";
                colors = CAINIAO_COLORS;
                break;
            case PINDUODUO:
                title = "拼多多身份码";
                subtitle = "向多多买菜代收点工作人员出示";
                colors = PDD_COLORS;
                break;
            case TAOBAO:
            default:
                title = "淘宝驿站身份码";
                subtitle = "向驿站工作人员出示";
                colors = TAOBAO_COLORS;
                break;
        }

        GradientDrawable cardBackground = gradient(colors, 22);
        identityCard.setBackground(cardBackground);
        identityTitle.setText(title);
        identitySubtitle.setText(subtitle);
        restyleSegmentRow(segmentRow);
    }

    private void restyleSegmentRow(LinearLayout segmentRow) {
        if (segmentRow == null) {
            return;
        }
        for (int i = 0; i < segmentRow.getChildCount(); i++) {
            View child = segmentRow.getChildAt(i);
            if (child instanceof LinearLayout && child.getTag() instanceof Destination) {
                Destination destination = (Destination) child.getTag();
                boolean selected = destination == selectedIdentity;
                GradientDrawable background = rounded(selected ? Color.rgb(247, 250, 252) : CARD_WHITE, 999);
                background.setStroke(dp(2), selected ? colorsFor(destination)[1] : LINE_COLOR);
                child.setBackground(background);
                child.setPadding(dp(6), dp(10), dp(6), dp(10));
            }
        }
    }

    private int[] colorsFor(Destination destination) {
        switch (destination) {
            case CAINIAO:
                return CAINIAO_COLORS;
            case PINDUODUO:
            case PINDUODUO_PENDING:
                return PDD_COLORS;
            case JD:
                return JD_COLORS;
            case XHS:
                return XHS_COLORS;
            case DOUYIN:
                return DOUYIN_COLORS;
            case TAOBAO:
            case TAOBAO_PENDING:
            default:
                return TAOBAO_COLORS;
        }
    }

    private View identityListItem(
            Destination destination,
            String title,
            String subtitle,
            int[] colors
    ) {
        LinearLayout item = new LinearLayout(this);
        item.setOrientation(LinearLayout.HORIZONTAL);
        item.setGravity(Gravity.CENTER_VERTICAL);
        item.setPadding(dp(16), dp(13), dp(14), dp(13));
        item.setBackground(rounded(CARD_WHITE, 18));
        item.setClickable(true);
        item.setFocusable(true);
        item.setContentDescription("打开" + destination.title);
        item.setOnClickListener(v -> DeepLinkLauncher.open(this, destination));

        LinearLayout.LayoutParams itemParams = verticalParams(dp(10));
        item.setLayoutParams(itemParams);

        TextView mark = text(destination.mark, 14, Color.WHITE, Typeface.BOLD);
        mark.setGravity(Gravity.CENTER);
        mark.setBackground(gradient(colors, 11));
        item.addView(mark, new LinearLayout.LayoutParams(dp(36), dp(36)));

        LinearLayout labels = new LinearLayout(this);
        labels.setOrientation(LinearLayout.VERTICAL);
        labels.setPadding(dp(13), 0, dp(10), 0);

        TextView titleView = text(title, 14, TEXT_PRIMARY, Typeface.BOLD);
        labels.addView(titleView);

        TextView subtitleView = text(subtitle, 11, TEXT_SECONDARY, Typeface.NORMAL);
        LinearLayout.LayoutParams subtitleParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        subtitleParams.topMargin = dp(2);
        subtitleView.setLayoutParams(subtitleParams);
        labels.addView(subtitleView);

        item.addView(labels, new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        TextView arrow = text("›", 24, colors[1], Typeface.NORMAL);
        arrow.setGravity(Gravity.CENTER);
        item.addView(arrow, new LinearLayout.LayoutParams(dp(26), dp(36)));

        return item;
    }

    // =====================================================================
    // 通用小组件
    // =====================================================================

    private TextView sectionTitle(String value, int topMarginDp) {
        TextView view = text(value, 17, TEXT_PRIMARY, Typeface.BOLD);
        LinearLayout.LayoutParams params = verticalParams(topMarginDp);
        params.bottomMargin = dp(10);
        view.setLayoutParams(params);
        return view;
    }

    private View hintCard(String emoji, String message) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.HORIZONTAL);
        card.setGravity(Gravity.CENTER_VERTICAL);
        card.setPadding(dp(16), dp(14), dp(16), dp(14));
        card.setLayoutParams(verticalParams(dp(12)));

        GradientDrawable background = rounded(Color.rgb(242, 249, 252), 18);
        background.setStroke(dp(1), Color.rgb(211, 230, 239));
        card.setBackground(background);

        TextView emojiView = text(emoji, 20, TEXT_PRIMARY, Typeface.NORMAL);
        LinearLayout.LayoutParams emojiParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        emojiParams.rightMargin = dp(10);
        emojiView.setLayoutParams(emojiParams);
        card.addView(emojiView);

        TextView messageView = text(message, 12, Color.rgb(61, 92, 110), Typeface.NORMAL);
        messageView.setLineSpacing(0, 1.5f);
        card.addView(messageView, new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        return card;
    }

    private void addPinButton(
            LinearLayout parent,
            Destination destination,
            String label
    ) {
        TextView button = text(label, 13, TEXT_PRIMARY, Typeface.BOLD);
        button.setGravity(Gravity.CENTER);
        GradientDrawable background = rounded(CARD_WHITE, 10);
        background.setStroke(dp(1), LINE_COLOR);
        button.setBackground(background);
        button.setClickable(true);
        button.setFocusable(true);
        button.setContentDescription("将" + destination.title + "添加到桌面");
        button.setOnClickListener(view -> ShortcutPinning.request(this, destination));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, dp(46), 1f);
        if (parent.getChildCount() > 0) {
            params.leftMargin = dp(8);
        }
        parent.addView(button, params);
    }

    private TextView text(String value, float sizeSp, int color, int style) {
        TextView view = new TextView(this);
        view.setText(value);
        view.setTextSize(sizeSp);
        view.setTextColor(color);
        view.setTypeface(Typeface.create("sans", style));
        view.setIncludeFontPadding(false);
        return view;
    }

    private LinearLayout.LayoutParams verticalParams(int topMarginDp) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.topMargin = dp(topMarginDp);
        return params;
    }

    private LinearLayout.LayoutParams horizontalWeightParams(int leftMarginDp) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
        );
        params.leftMargin = dp(leftMarginDp);
        return params;
    }

    private GradientDrawable rounded(int color, int radiusDp) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(dp(radiusDp));
        return drawable;
    }

    private GradientDrawable gradient(int[] colors, int radiusDp) {
        GradientDrawable drawable = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                colors
        );
        drawable.setCornerRadius(dp(radiusDp));
        return drawable;
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}
