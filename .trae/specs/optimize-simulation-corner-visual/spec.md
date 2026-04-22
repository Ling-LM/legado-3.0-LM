# 仿真翻页右下角视觉效果优化 Spec

## Why
仿真翻页动画在右上角（RT→LB）和右下角（RB→LT）两个对角方向的视觉表现不一致，右下角翻页效果不如右上角自然美观。根本原因在于：阴影绘制时未根据 `mIsRtOrLb`（是否为右上-左下方向）正确切换渐变方向，导致右下角翻页时的折叠阴影、前影、背影的方向与物理预期不符，视觉上产生不协调感。

## What Changes
- **修复 drawCurrentPageShadow() 第二段阴影（垂直方向）的渐变方向**：当 `mIsRtOrLb = false`（即右下角翻页）时，垂直阴影应使用 BOTTOM_TOP 方向而非 TOP_BOTTOM
- **修复 drawCurrentBackArea() 中折叠阴影的渐变方向**：确保非 RT/LB 方向时使用正确的左右翻转渐变
- **统一所有阴影绘制逻辑与参考实现 (SimulationPageAnim.java) 一致**
- 保持已有的性能优化（LinearGradient + Paint），不回退到 GradientDrawable

## Impact
- Affected specs: 仿真翻页动画渲染管线（视觉效果）
- Affected code:
  - `app/src/main/java/io/legado/app/ui/book/read/page/delegate/SimulationPageDelegate.kt`

## ADDED Requirements

### Requirement: 右上角与右下角翻页效果视觉一致
系统 SHALL 确保从右上角和右下角发起的对角翻页动画具有一致的视觉质量，包括阴影方向、渐变过渡和整体美感。

#### Scenario: 右下角翻页（RB→LT）
- **WHEN** 用户从屏幕右下角发起翻页手势（mCornerX = screenWidth, mCornerY = screenHeight, mIsRtOrLb = false）
- **THEN** 翻页过程中的折叠阴影、前面阴影、背面阴影的方向应与物理翻页方向匹配，视觉效果与右上角翻页对称且协调

#### Scenario: 右上角翻页（RT→LB）
- **WHEN** 用户从屏幕右上角发起翻页手势（mCornerX = screenWidth, mCornerY = 0, mIsRtOrLb = true）
- **THEN** 翻页效果保持现有良好表现，不受修改影响

### Requirement: 垂直阴影渐变方向正确切换
系统 SHALL 在绘制第二段前面阴影（垂直方向）时，根据 `mIsRtOrLb` 标志选择正确的渐变方向。

#### Scenario: mIsRtOrLb = true 时垂直阴影
- **WHEN** 翻页方向为右上→左下或左下→右上
- **THEN** 垂直阴影渐变方向为 TOP_BOTTOM（从上到下变淡）

#### Scenario: mIsRtOrLb = false 时垂直阴影
- **WHEN** 翻页方向为左上→右下或右下→左上
- **THEN** 垂直阴影渐变方向为 BOTTOM_TOP（从下到上变淡）

### Requirement: 折叠阴影方向与翻页方向匹配
系统 SHALL 在绘制翻起页面背面的折叠阴影时，根据 `mIsRtOrLb` 选择正确的水平渐变方向。

#### Scenario: RT/LB 方向折叠阴影
- **WHEN** mIsRtOrLb = true
- **THEN** 折叠阴影使用 LEFT_RIGHT 渐变方向

#### Scenario: 非 RT/LB 方向折叠阴影
- **WHEN** mIsRtOrLb = false
- **THEN** 折叠阴影使用 RIGHT_LEFT 渐变方向

## MODIFIED Requirements

### Requirement: drawCurrentPageShadow() 阴影绘制逻辑
修改第二段阴影（基于 mBezierControl2 的垂直阴影）的绘制代码，增加对 `mIsRtOrLb` 的判断，动态选择 LinearGradient 的 y1/y2 参数顺序以控制渐变方向。

### Requirement: drawCurrentBackArea() 折叠阴影绘制逻辑
确认并修正折叠阴影的 LinearGradient 方向参数，使其与参考实现一致。

## REMOVED Requirements
无