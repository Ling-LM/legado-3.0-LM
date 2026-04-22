# Tasks

- [x] Task 1: 修复 drawCurrentPageShadow() 第二段垂直阴影的渐变方向
  - [x] 分析当前实现中第二段阴影的 LinearGradient 方向参数
  - [x] 根据 mIsRtOrLb 条件切换 y1/y2 参数顺序（TOP_BOTTOM vs BOTTOM_TOP）
  - [x] 确保与参考实现 SimulationPageAnim.java 的 mFrontShadowDrawableHTB/HBT 逻辑一致

- [x] Task 2: 验证并修复 drawCurrentBackArea() 折叠阴影的渐变方向
  - [x] 对比参考实现中 mFolderShadowDrawableLR/RL 的使用逻辑
  - [x] 确认当前实现的 x1/x2 参数顺序在 mIsRtOrLb = false 时是否正确
  - [x] 如有偏差则修正 LinearGradient 的方向参数

- [x] Task 3: 验证 drawNextPageAreaAndShadow() 背面阴影的方向一致性
  - [x] 确认背面阴影在不同翻页方向下的表现符合预期
  - [x] 如需调整则同步修改

- [ ] Task 4: 构建验证与视觉测试
  - [ ] 执行 ./gradlew assembleAppDebug 确保编译通过
  - [ ] 验证右上角和右下角翻页效果视觉一致性

# Task Dependencies
- Task 1 独立，是核心修复（影响最大）
- Task 2 独立，可与 Task 1 并行
- Task 3 依赖 Task 1 和 Task 2 完成后验证
- Task 4 依赖 Task 1/2/3 全部完成