# 底部导航栏紧凑高亮显示修复 Spec

## Why
将底部导航栏的 `labelVisibilityMode` 从 `unlabeled` 改为 `selected` 后，虽然实现了标题高亮显示功能，但导航栏高度明显增加，导致整体布局不够紧凑，占用过多屏幕空间。需要通过缩小图标和文字尺寸来优化布局，在保持标题高亮效果的同时维持导航栏的紧凑性。

## What Changes
- 修改 `ThemeBottomNavigationVIew.kt`：在初始化时设置更小的图标尺寸和文字尺寸
- 调整 `activity_main.xml` 中的导航栏属性（如需要）
- 确保选中项标题以高亮样式显示（颜色变化），未选中项保持默认样式
- 保持导航栏的核心交互功能和视觉协调性

## Impact
- Affected specs: 底部导航栏UI组件
- Affected code:
  - `app/src/main/java/io/legado/app/lib/theme/view/ThemeBottomNavigationVIew.kt`
  - `app/src/main/res/layout/activity_main.xml`

## ADDED Requirements

### Requirement: 导航栏紧凑高亮显示
系统 SHALL 在保持标题高亮显示的同时，确保导航栏高度与修改前（unlabeled模式）基本一致或仅略微增加。

#### Scenario: 选中导航项时显示高亮标题
- **WHEN** 用户点击底部导航栏的某个选项
- **THEN** 该选项应同时显示图标和标题文字，标题使用主题强调色高亮显示
- **AND** 导航栏整体高度保持紧凑（目标高度 ≤ 60dp）

#### Scenario: 未选中项保持简洁
- **WHEN** 导航栏中存在未选中的选项
- **THEN** 未选中项仅显示图标（不显示标题）或显示缩小后的标题
- **AND** 整体布局保持对称和协调

### Requirement: 图标和文字尺寸优化
系统 SHALL 通过调整 BottomNavigationView 的图标尺寸和文字大小来实现紧凑布局。

#### Scenario: 图标尺寸调整
- **WHEN** 导航栏渲染时
- **THEN** 图标尺寸应从默认的 24dp 缩小至 18-20dp 范围

#### Scenario: 文字尺寸调整
- **WHEN** 选中项显示标题时
- **THEN** 标题文字大小应在 10-12sp 范围内，确保可读性与紧凑性的平衡

### Requirement: 多设备适配
系统 SHALL 确保修改后的导航栏在不同屏幕尺寸和密度的设备上均能正常显示。

#### Scenario: 不同设备显示
- **WHEN** 应用运行在不同设备上（手机、平板等）
- **THEN** 导航栏布局应自适应，无溢出或裁剪问题

## MODIFIED Requirements

### Requirement: ThemeBottomNavigationVIew 初始化逻辑
在 `init` 块中增加对 `itemIconSize` 和相关文字/间距属性的设置，实现紧凑布局。

### Requirement: activity_main.xml 布局属性
根据需要调整 `minHeight`、`padding` 或其他属性以配合代码层面的尺寸调整。