# Tasks

- [x] Task 1: 修改 ThemeBottomNavigationVIew.kt 添加紧凑布局配置
  - [x] 在 init 块中设置 itemIconSize 为较小值（20dp）
  - [x] 设置合适的文字尺寸（通过 itemTextAppearanceActive/Inactive）
  - [x] 设置 labelVisibilityMode 为 LABELED 模式
  - [x] 添加必要的 import 语句

- [x] Task 2: 调整 activity_main.xml 布局属性
  - [x] 将 labelVisibilityMode 从 "selected" 改为 "labeled"
  - [x] 微调 minHeight 为 52dp

- [x] Task 3: 构建验证
  - [x] 执行 ./gradlew assembleAppDebug 确保编译通过
  - [x] 验证导航栏高度符合要求

# Task Dependencies
- Task 1 是核心任务，必须先完成 ✅
- Task 2 依赖 Task 1 的结果进行微调 ✅
- Task 3 依赖 Task 1 和 Task 2 完成 ✅