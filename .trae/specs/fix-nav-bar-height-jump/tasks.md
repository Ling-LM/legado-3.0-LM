# Tasks

- [ ] Task 1: 修改 activity_main.xml 的 labelVisibilityMode
  - [ ] 将 labelVisibilityMode 从 "selected" 改为 "labeled"
  - [ ] 确认 minHeight 属性保持合理值（52dp）

- [ ] Task 2: 增强 ThemeBottomNavigationVIew.kt 的紧凑布局配置
  - [ ] 在 init 块中设置 itemIconSize 为 20dp
  - [ ] 设置 itemTextAppearanceActive（选中文字样式，11sp）
  - [ ] 设置 itemTextAppearanceInactive（未选中文字样式，10sp）
  - [ ] 添加必要的 import 和资源引用

- [ ] Task 3: 构建验证
  - [ ] 执行 ./gradlew assembleAppDebug 确保编译通过
  - [ ] 确认导航栏高度恒定（无跳动）

# Task Dependencies
- Task 1 是核心修改，必须先完成
- Task 2 依赖 Task 1 的结果进行尺寸微调
- Task 3 依赖 Task 1 和 Task 2 全部完成