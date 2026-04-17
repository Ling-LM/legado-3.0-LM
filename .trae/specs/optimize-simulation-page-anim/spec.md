# 仿真翻页动画流畅度优化 Spec

## Why
仿真翻页动画存在整体速度过慢、右上和右下区域明显卡顿的问题，影响阅读体验。根本原因在于：每帧绘制过程中存在大量 `clipPath` 操作、`GradientDrawable` 阴影绘制开销、`PointF` 对象频繁分配、以及动画时长计算不合理，导致 GPU 负担过重，尤其在右上/右下区域（对角线翻页路径最长、绘制区域最大时）卡顿最为明显。

## What Changes
- 优化 `calcPoints()` 中的对象分配：将 `getCross()` 方法改为复用已有 `PointF` 对象，避免每帧创建新对象
- 优化阴影绘制：将 `GradientDrawable` 阴影替换为基于 `LinearGradient` + `Paint` 的直接绘制方式，减少 `setBounds()` + `draw()` 的开销
- 减少 `clipPath` 调用次数：合并可复用的裁剪路径，减少 `canvas.save()/restore()` 嵌套层级
- 优化 `drawCurrentBackArea()` 中的 `drawColor` 调用，避免不必要的全区域填充
- 优化动画时长计算：针对仿真翻页场景调整 `startScroll` 的 duration 计算逻辑，使动画速度更合理
- 预计算和缓存可复用值，减少重复计算

## Impact
- Affected specs: 仿真翻页动画渲染管线
- Affected code:
  - `app/src/main/java/io/legado/app/ui/book/read/page/delegate/SimulationPageDelegate.kt`
  - `app/src/main/java/io/legado/app/ui/book/read/page/delegate/PageDelegate.kt`（动画时长计算）

## ADDED Requirements

### Requirement: 仿真翻页动画帧率达标
系统 SHALL 确保仿真翻页动画在所有翻页方向（包括右上→左下、右下→左上对角翻页）下均能保持流畅，无明显卡顿。

#### Scenario: 右上/右下区域对角翻页
- **WHEN** 用户从屏幕右上角或右下角发起翻页手势
- **THEN** 动画帧率应与水平翻页时保持一致，无可见卡顿和掉帧

### Requirement: 减少动画帧内对象分配
系统 SHALL 在动画绘制过程中避免创建新的 `PointF` 对象，复用预分配的对象以减少 GC 压力。

#### Scenario: calcPoints 计算贝塞尔曲线交点
- **WHEN** 每帧调用 `calcPoints()` 计算贝塞尔曲线端点
- **THEN** `getCross()` 方法应将结果写入预分配的 `PointF` 对象，而非创建新对象

### Requirement: 优化阴影绘制性能
系统 SHALL 使用 `LinearGradient` + `Paint` 直接绘制阴影，替代 `GradientDrawable` 的 `setBounds()` + `draw()` 方式。

#### Scenario: 绘制翻页阴影
- **WHEN** 每帧绘制翻页阴影效果
- **THEN** 阴影通过 `Paint` 的 `Shader` 直接绘制，避免 `GradientDrawable` 的每帧 bounds 设置和绘制开销

### Requirement: 动画速度合理
系统 SHALL 确保仿真翻页动画的整体速度与用户手势跟手，自动翻页时动画时长不超过 400ms。

#### Scenario: 自动翻页动画
- **WHEN** 用户通过按键或自动翻页触发仿真翻页
- **THEN** 动画总时长应在 300-400ms 范围内，视觉上流畅自然

## MODIFIED Requirements

### Requirement: 仿真翻页绘制管线
将 `SimulationPageDelegate.onDraw()` 中的四步绘制流程（drawCurrentPageArea → drawNextPageAreaAndShadow → drawCurrentPageShadow → drawCurrentBackArea）优化为减少 canvas 状态切换和裁剪操作的版本，同时保持视觉效果一致。
