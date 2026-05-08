# Checklist

- [ ] activity_main.xml 中 labelVisibilityMode 改为 "labeled"
- [ ] ThemeBottomNavigationVIew.kt 中 itemIconSize 设置为 20dp
- [ ] 选中项文字样式 11sp（通过 itemTextAppearanceActive）
- [ ] 未选中项文字样式 10sp（通过 itemTextAppearanceInactive）
- [ ] 所有导航项始终显示图标+标签（高度恒定）
- [ ] 切换导航项时无高度跳动
- [ ] 选中项使用主题强调色高亮
- [ ] 未选中项使用次要颜色
- [ ] 编译通过：./gradlew assembleAppDebug 成功（exit code 0）
- [ ] 导航栏整体高度 ≤ 60dp（紧凑）