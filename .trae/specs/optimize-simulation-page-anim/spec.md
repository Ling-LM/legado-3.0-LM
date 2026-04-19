# 仿真翻页动画性能优化 Spec

## Why
当前仿真翻页动画存在流畅度不足的问题，主要表现为：
1. **整体动画速度过慢**：动画运行不够流畅，影响阅读体验
2. **右上/右下区域卡顿明显**：在特定区域出现明显的卡顿现象，严重影响用户体验

这些性能问题会导致用户在阅读时感到不顺畅，降低应用的整体质量。

## What Changes
- **优化对象创建**：减少每帧创建的对象数量，避免 GC 压力
- **缓存计算结果**：对重复计算进行缓存，减少不必要的数学运算
- **优化绘制流程**：减少冗余的 Canvas 操作和状态切换
- **改进算法效率**：优化贝塞尔曲线计算和交点求解算法
- **减少内存分配**：复用对象实例，特别是 PointF、Path 等

**注意**：保持原有动画效果不变，仅优化性能

## Impact
- Affected specs: 页面翻页动画系统
- Affected code:
  - `app/src/main/java/io/legado/app/ui/book/read/page/delegate/SimulationPageDelegate.kt` (主要优化目标)
  - 可能涉及 `HorizontalPageDelegate.kt` (基类)

## ADDED Requirements

### Requirement: 性能优化的仿真翻页动画
系统 SHALL 提供高性能的仿真翻页动画实现，满足以下要求：

#### Scenario: 流畅的翻页动画体验
- **WHEN** 用户执行翻页操作（上一页/下一页）
- **THEN** 动画 SHOULD 在 16ms 内完成每帧渲染（达到 60fps 标准）
- **AND** 动画过程 SHOULD 无明显卡顿或掉帧
- **AND** 右上角和右下角区域 SHOULD 与其他区域一样流畅

#### Scenario: 对象复用与内存优化
- **WHEN** 动画执行过程中
- **THEN** 系统 SHOULD 复用 PointF、Path 等对象实例
- **AND** 不应在每帧创建新的临时对象
- **AND** 内存分配 SHOULD 保持稳定，避免频繁 GC

#### Scenario: 计算性能优化
- **WHEN** 执行贝塞尔曲线计算和交点求解
- **THEN** 系统 SHOULD 使用高效的算法实现
- **AND** 避免重复计算相同的值
- **AND** 数学运算 SHOULD 经过优化以减少 CPU 开销

## MODIFIED Requirements

### Requirement: SimulationPageDelegate 核心绘制逻辑
原有的 `onDraw()` 方法需要重构以满足性能要求：

1. **`drawCurrentPageArea()`**: 优化 Path 构建和 clip 操作
2. **`drawNextPageAreaAndShadow()`**: 减少不必要的 GradientDrawable bounds 设置
3. **`drawCurrentPageShadow()`**: 优化双重阴影绘制逻辑
4. **`drawCurrentBackArea()`**: 优化矩阵变换和颜色过滤器的使用
5. **`calcPoints()`**: 消除对象创建，优化数学计算
6. **`getCross()`**: 复用 PointF 对象

### Requirement: 动画参数配置
保持原有动画参数不变：
- 贝塞尔曲线控制点计算逻辑
- 阴影渐变颜色和方向
- 翻页角度和距离计算公式
- 触摸响应区域判断逻辑

## REMOVED Requirements
无（所有功能保留，仅优化实现）
