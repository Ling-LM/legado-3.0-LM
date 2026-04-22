# Tasks

## 优化任务列表

- [ ] Task 1: 对象复用优化 - 消除每帧对象创建
  - [ ] 1.1 复用 `getCross()` 方法中的 PointF 对象，改为成员变量复用
  - [ ] 1.2 预分配所有需要的临时变量和中间计算结果存储
  - [ ] 1.3 移除 `calcPoints()` 和绘制方法中的临时对象创建

- [ ] Task 2: 数学计算性能优化
  - [ ] 2.1 缓存重复计算的值（如 hypot、atan2 等结果）
  - [ ] 2.2 优化 `calcPoints()` 中的贝塞尔曲线控制点计算逻辑
  - [ ] 2.3 减少不必要的类型转换（Double/Float 转换）
  - [ ] 2.4 使用更高效的数学运算方式

- [ ] Task 3: 绘制流程优化
  - [ ] 3.1 减少 Canvas 状态保存/恢复次数
  - [ ] 3.2 优化 clipPath 操作，合并可以合并的裁剪区域
  - [ ] 3.3 优化 GradientDrawable 的 bounds 设置频率
  - [ ] 3.4 减少不必要的 Paint 属性设置

- [ ] Task 4: 特定区域卡顿问题修复
  - [ ] 4.1 分析右上角和右下角卡顿的根本原因
  - [ ] 4.2 优化边界条件处理（mBezierStart1.x 越界情况）
  - [ ] 4.3 优化 mIsRtOrLb 分支的性能差异
  - [ ] 4.4 测试并验证右上/右下区域的流畅度

- [ ] Task 5: 动画速度调优
  - [ ] 5.1 检查动画时间参数配置
  - [ ] 5.2 评估是否需要调整插值器或动画时长
  - [ ] 5.3 确保 onAnimStart() 中的滚动参数合理
  - [ ] 5.4 测试整体动画流畅度，确保达到 60fps 标准

- [ ] Task 6: 代码重构与整理
  - [ ] 6.1 整理优化后的代码结构，提高可读性
  - [ ] 6.2 添加必要的性能优化注释
  - [ ] 6.3 确保代码符合 Kotlin 最佳实践
  - [ ] 6.4 验证动画视觉效果与原版一致

# Task Dependencies
- [Task 2] 依赖于 [Task 1] (先完成对象复用，再优化计算)
- [Task 4] 依赖于 [Task 2] (需要先优化计算逻辑才能定位卡顿原因)
- [Task 5] 依赖于 [Task 3, Task 4] (需要先优化绘制流程和修复卡顿)
- [Task 6] 依赖于 [Task 1-5] (最后进行代码整理)

## 并行执行建议
- Task 1 可以独立开始
- Task 2 在 Task 1 之后或并行（如果互不影响的部分）
- Task 3 可以与 Task 2 部分并行
- Task 4、5、6 必须按顺序执行
- [x] Task 1: 优化 calcPoints() 和 getCross() 消除每帧对象分配
  - [x] 将 mBezierEnd1 和 mBezierEnd2 从 `var` 改为预分配的 `val` PointF 对象
  - [x] 修改 getCross() 方法签名，接收输出 PointF 参数而非返回新对象
  - [x] 更新 calcPoints() 中调用 getCross() 的代码，传入预分配对象

- [x] Task 2: 将 GradientDrawable 阴影替换为 LinearGradient + Paint 直接绘制
  - [x] 创建基于 LinearGradient 的阴影 Paint 对象，替代 8 个 GradientDrawable 成员变量
  - [x] 重写 drawCurrentPageShadow() 使用 Paint 直接绘制阴影
  - [x] 重写 drawNextPageAreaAndShadow() 中的背面阴影绘制
  - [x] 重写 drawCurrentBackArea() 中的折叠阴影绘制

- [x] Task 3: 优化 canvas 裁剪操作减少 save/restore 嵌套
  - [x] 在 drawCurrentPageShadow() 中合并两次独立的 clipPath+draw 操作，减少 save/restore 次数
  - [x] 在 drawCurrentBackArea() 中移除不必要的 canvas.drawColor() 调用
  - [x] 使用 try-finally 确保 canvas.restore() 始终被调用

- [x] Task 4: 优化动画时长计算
  - [x] 在 SimulationPageDelegate.onAnimStart() 中调整 dx/dy 计算，使对角翻页动画时长合理
  - [x] 确保 startScroll 的 duration 在 300-400ms 范围内

- [x] Task 5: 预计算和缓存可复用值
  - [x] 将 drawCurrentPageShadow() 中的阴影宽度常量 25 提取为类级别常量
  - [x] 将 1.414（√2 近似值）提取为常量
  - [x] 缓存 calcPoints() 中重复使用的中间计算结果

# Task Dependencies
- Task 1 独立，可先执行
- Task 2 独立，可与 Task 1 并行
- Task 3 依赖 Task 2（阴影绘制方式改变后裁剪逻辑需同步调整）
- Task 4 独立，可与 Task 1/2 并行
- Task 5 独立，可与其它任务并行
