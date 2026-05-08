# Checklist

- [x] labelVisibilityMode 改为 "labeled" 模式（activity_main.xml:23）
- [x] 选中项显示高亮标题文字（使用主题强调色 - ThemeBottomNavigationVIew.kt:33-35）
- [x] 未选中项也显示标题但样式不同（10sp + 次要颜色）
- [x] 导航栏高度恒定一致（~56dp，不再跳动）
- [x] 图标尺寸调整至 20dp（ThemeBottomNavigationVIew.kt:44）
- [x] 选中文字 11sp（styles.xml BottomNavTextStyle_Active）
- [x] 未选中文字 10sp（styles.xml BottomNavTextStyle_Inactive）
- [x] 编译通过：./gradlew assembleAppDebug 成功（exit code 0）
- [x] 导航栏交互功能正常（点击切换、页面联动）