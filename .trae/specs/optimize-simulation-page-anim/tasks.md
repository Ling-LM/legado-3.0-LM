# Tasks

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
