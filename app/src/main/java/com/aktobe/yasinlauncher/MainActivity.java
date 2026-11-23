package com.aktobe.yasinlauncher;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class MainActivity extends Activity {
    private final int BG = Color.rgb(7, 10, 18);
    private final int WHITE = Color.rgb(246, 248, 255);
    private final int MUTED = Color.rgb(164, 175, 196);
    private final Handler handler = new Handler();
    private TextView clock;
    private LinearLayout appRow;

    private int dp(float value) { return (int)(value * getResources().getDisplayMetrics().density + 0.5f); }

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);
        getWindow().getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        buildScreen();
        updateClock();
    }

    private GradientDrawable gradient(int a, int b, float radius) {
        GradientDrawable d = new GradientDrawable(GradientDrawable.Orientation.TL_BR, new int[]{a,b});
        d.setCornerRadius(dp(radius));
        return d;
    }

    private TextView text(String value, int size, int color, boolean bold) {
        TextView t = new TextView(this);
        t.setText(value); t.setTextSize(size); t.setTextColor(color);
        t.setGravity(Gravity.CENTER_VERTICAL);
        t.setTypeface(Typeface.create("sans-serif", bold ? Typeface.BOLD : Typeface.NORMAL));
        return t;
    }

    private LinearLayout column() {
        LinearLayout l = new LinearLayout(this); l.setOrientation(LinearLayout.VERTICAL); return l;
    }

    private LinearLayout.LayoutParams lp(int w, int h) { return new LinearLayout.LayoutParams(w < 0 ? w : dp(w), h < 0 ? h : dp(h)); }

    private void buildScreen() {
        FrameLayout backdrop = new FrameLayout(this);
        backdrop.setBackground(new GradientDrawable(GradientDrawable.Orientation.TL_BR,
            new int[]{Color.rgb(12,22,43), Color.rgb(7,10,18), Color.rgb(5,7,13)}));
        ScrollView page = new ScrollView(this);
        page.setFillViewport(true);
        LinearLayout body = column();
        body.setPadding(dp(54), dp(28), dp(54), dp(28));
        page.addView(body, new ScrollView.LayoutParams(-1, -2));
        backdrop.addView(page, new FrameLayout.LayoutParams(-1,-1));
        setContentView(backdrop);

        LinearLayout top = new LinearLayout(this);
        top.setGravity(Gravity.CENTER_VERTICAL);
        TextView brand = text("YASIN", 21, WHITE, true);
        brand.setLetterSpacing(0.16f);
        top.addView(brand, lp(-2, 42));
        TextView edition = text("  TV", 18, Color.rgb(115,176,255), true);
        top.addView(edition, lp(-2,42));
        View gap = new View(this); top.addView(gap, new LinearLayout.LayoutParams(0,1,1));
        String[] tabs = {"Смотреть", "Фильмы", "Сериалы", "Приложения"};
        for (String tab : tabs) {
            TextView item = text(tab, 14, tab.equals("Смотреть") ? WHITE : MUTED, tab.equals("Смотреть"));
            item.setGravity(Gravity.CENTER);
            item.setPadding(dp(12),0,dp(12),0);
            item.setFocusable(true);
            item.setOnFocusChangeListener((v, f) -> { v.setAlpha(f ? 1f : .72f); v.animate().scaleX(f?1.06f:1f).scaleY(f?1.06f:1f).setDuration(130).start(); });
            top.addView(item, lp(-2,42));
        }
        View spacer = new View(this); top.addView(spacer, new LinearLayout.LayoutParams(dp(22),1));
        clock = text("", 14, WHITE, true); clock.setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL);
        top.addView(clock, lp(150,42));
        body.addView(top, lp(-1,48));

        FrameLayout hero = new FrameLayout(this);
        hero.setBackground(gradient(Color.rgb(31,76,128), Color.rgb(18,27,53), 24));
        hero.setPadding(dp(34),dp(22),dp(28),dp(22));
        LinearLayout heroText = column();
        TextView eyebrow = text("ВАШ ВЕЧЕР НАЧИНАЕТСЯ ЗДЕСЬ", 11, Color.rgb(190,218,255), true);
        eyebrow.setLetterSpacing(.18f);
        heroText.addView(eyebrow, lp(-1,24));
        TextView title = text("Большой экран. Большие истории.", 30, WHITE, true);
        heroText.addView(title, lp(-1,54));
        TextView sub = text("Любимые приложения и развлечения — в одном месте.", 15, Color.rgb(218,228,244), false);
        heroText.addView(sub, lp(-1,34));
        TextView hint = text("Выберите приложение ниже  •  Нажмите OK на пульте", 12, Color.rgb(186,207,234), false);
        heroText.addView(hint, lp(-1,32));
        hero.addView(heroText, new FrameLayout.LayoutParams(-1,-1,Gravity.CENTER_VERTICAL));
        TextView glow = text("", 112, Color.argb(55,255,255,255), true);
        glow.setGravity(Gravity.CENTER);
        FrameLayout.LayoutParams glp = new FrameLayout.LayoutParams(dp(190),-1,Gravity.RIGHT|Gravity.CENTER_VERTICAL);
        hero.addView(glow, glp);
        LinearLayout.LayoutParams heroLp = lp(-1,210); heroLp.topMargin=dp(20);
        body.addView(hero, heroLp);

        LinearLayout section = new LinearLayout(this); section.setGravity(Gravity.CENTER_VERTICAL);
        TextView appsTitle = text("Ваши приложения", 21, WHITE, true);
        section.addView(appsTitle, lp(-2,48));
        View push = new View(this); section.addView(push,new LinearLayout.LayoutParams(0,1,1));
        TextView all = text("ОТКРЫТЬ ВСЕ  ›", 11, Color.rgb(130,181,255), true);
        section.addView(all,lp(-2,48));
        LinearLayout.LayoutParams secLp=lp(-1,48); secLp.topMargin=dp(16); body.addView(section,secLp);

        HorizontalScrollView horizontal = new HorizontalScrollView(this);
        horizontal.setHorizontalScrollBarEnabled(false);
        appRow = new LinearLayout(this); appRow.setOrientation(LinearLayout.HORIZONTAL);
        horizontal.addView(appRow,new HorizontalScrollView.LayoutParams(-2,-1));
        body.addView(horizontal,lp(-1,156));
        populateApps();

        TextView quickTitle = text("Быстрый доступ", 18, WHITE, true);
        LinearLayout.LayoutParams qlp=lp(-1,42); qlp.topMargin=dp(12); body.addView(quickTitle,qlp);
        LinearLayout quick = new LinearLayout(this); quick.setOrientation(LinearLayout.HORIZONTAL);
        quick.addView(actionTile("⚙", "Настройки", () -> openSettings()), quickParams());
        quick.addView(actionTile("↻", "Обновить список", () -> populateApps()), quickParams());
        quick.addView(actionTile("◷", "Дата и время", () -> openDateSettings()), quickParams());
        body.addView(quick,lp(-1,76));

        TextView footer=text("YASIN  •  Пространство ваших развлечений",11,Color.rgb(102,117,143),false);
        LinearLayout.LayoutParams flp=lp(-1,28); flp.topMargin=dp(10); body.addView(footer,flp);
    }

    private LinearLayout.LayoutParams quickParams() {
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(dp(210),dp(68)); p.rightMargin=dp(12); return p;
    }

    private View actionTile(String symbol, String label, Runnable action) {
        LinearLayout tile = new LinearLayout(this); tile.setGravity(Gravity.CENTER_VERTICAL);
        tile.setPadding(dp(18),0,dp(18),0); tile.setBackground(gradient(Color.rgb(26,34,51),Color.rgb(17,24,38),14));
        TextView icon=text(symbol,24,Color.rgb(151,194,255),true); tile.addView(icon,lp(38,-1));
        TextView title=text(label,14,WHITE,true); tile.addView(title,lp(-2,-1));
        tile.setFocusable(true); tile.setOnClickListener(v->action.run()); focusStyle(tile); return tile;
    }

    private void focusStyle(View v) {
        v.setOnFocusChangeListener((view, focused) -> {
            view.animate().scaleX(focused?1.055f:1f).scaleY(focused?1.055f:1f).alpha(focused?1f:.88f).setDuration(140).start();
            view.setElevation(dp(focused?12:2));
            view.setBackground(gradient(focused?Color.rgb(53,91,143):Color.rgb(25,34,51),
                focused?Color.rgb(31,55,91):Color.rgb(16,23,37),14));
        });
    }

    private void populateApps() {
        if (appRow == null) return;
        appRow.removeAllViews();
        PackageManager pm=getPackageManager();
        Intent leanback=new Intent(Intent.ACTION_MAIN); leanback.addCategory(Intent.CATEGORY_LEANBACK_LAUNCHER);
        Intent normal=new Intent(Intent.ACTION_MAIN); normal.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> found=new ArrayList<>();
        Set<String> seen=new HashSet<>();
        for (ResolveInfo ri : pm.queryIntentActivities(leanback,0)) {
            String p=ri.activityInfo.packageName;
            if (!p.equals(getPackageName()) && seen.add(p)) found.add(ri);
        }
        for (ResolveInfo ri : pm.queryIntentActivities(normal,0)) {
            String p=ri.activityInfo.packageName;
            if (!p.equals(getPackageName()) && seen.add(p)) found.add(ri);
        }
        Collections.sort(found,(a,b)->String.CASE_INSENSITIVE_ORDER.compare(
            String.valueOf(a.loadLabel(pm)),String.valueOf(b.loadLabel(pm))));
        int count=0;
        for (ResolveInfo ri : found) {
            if (count++ >= 14) break;
            LinearLayout card=column(); card.setGravity(Gravity.CENTER);
            card.setPadding(dp(10),dp(8),dp(10),dp(8));
            card.setBackground(gradient(Color.rgb(25,34,51),Color.rgb(15,22,36),15));
            ImageView icon=new ImageView(this);
            try { icon.setImageDrawable(ri.loadIcon(pm)); } catch(Exception ignored) {}
            icon.setScaleType(ImageView.ScaleType.FIT_CENTER);
            card.addView(icon,lp(58,58));
            TextView name=text(String.valueOf(ri.loadLabel(pm)),12,WHITE,true);
            name.setGravity(Gravity.CENTER); name.setMaxLines(1); name.setEllipsize(android.text.TextUtils.TruncateAt.END);
            LinearLayout.LayoutParams np=lp(108,28); np.topMargin=dp(6); card.addView(name,np);
            String packageName=ri.activityInfo.packageName;
            String className=ri.activityInfo.name;
            card.setFocusable(true); card.setClickable(true);
            card.setOnClickListener(v -> {
                try {
                    Intent launch=new Intent(Intent.ACTION_MAIN);
                    launch.addCategory(Intent.CATEGORY_LAUNCHER);
                    launch.setClassName(packageName,className);
                    launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(launch);
                } catch(Exception e) {
                    Intent fallback=pm.getLaunchIntentForPackage(packageName);
                    if(fallback!=null) startActivity(fallback);
                }
            });
            focusStyle(card);
            LinearLayout.LayoutParams cp=lp(132,138); cp.rightMargin=dp(12); appRow.addView(card,cp);
        }
        if (found.isEmpty()) {
            TextView empty=text("Приложения не найдены",14,MUTED,false); appRow.addView(empty,lp(260,80));
        }
    }

    private void openSettings() {
        try { startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS)); }
        catch(Exception e) { startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS)); }
    }
    private void openDateSettings() {
        try { startActivity(new Intent(android.provider.Settings.ACTION_DATE_SETTINGS)); }
        catch(Exception e) { openSettings(); }
    }
    private void updateClock() {
        if(clock!=null) clock.setText(new SimpleDateFormat("EEE, d MMM  •  HH:mm",new Locale("ru")).format(new Date()));
        handler.postDelayed(this::updateClock,30000);
    }
    @Override protected void onDestroy() { handler.removeCallbacksAndMessages(null); super.onDestroy(); }
}
