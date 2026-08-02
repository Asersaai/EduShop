package com.example.shop.entity.enums;

public enum Category {
        PHONES("Телефоны и гаджеты", "📱"),
        COMPUTERS("Компьютеры и ноутбуки", "💻"),
        COMPONENTS("Комплектующие", "🖥️"),
        GAMING("Игры и консоли", "🎮"),
        TV_AUDIO_VIDEO("ТВ, аудио и видео", "📺"),
        HOME_APPLIANCES("Бытовая техника", "🏠"),
        FURNITURE("Мебель", "🪑"),
        CLOTHING("Одежда", "👕"),
        SHOES("Обувь", "👟"),
        ACCESSORIES("Аксессуары", "⌚"),
        BEAUTY("Красота и здоровье", "💄");

    private final String displayName;
    private final String icon;
    Category(String displayName, String icon) {
        this.displayName = displayName;
        this.icon = icon;
    }

    public String getDisplayName() {
        return displayName;
    }
    public  String getIcon() {
        return icon;
    }
}
