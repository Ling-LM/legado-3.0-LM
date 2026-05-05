# 底部导航栏高度跳动修复 Spec

## Why
当前底部导航栏使用 `labelVisibilityMode="selected"` 模式，导致选中项显示"图标+文字"而未选中项仅显示"图标"。当用户切换导航选项时，导航栏高度在两种状态间变化，产生明显的**忽高忽低跳动现象**，严重影响视觉体验和交互流畅度。

参考项目 (legado-MD3-LM3.0) 使用 Compose 的 `NavigationBarItem(alwaysShowLabel = true)` 实现了高度恒定的效果。

## What Changes
- **修改 `activity_main.xml`**：将 `labelVisibilityMode` 从 `"selected"` 改为 `"labeled"`
- **优化 `ThemeBottomNavigationVIew.kt`**：添加紧凑布局配置（图标/文字尺寸调整）
- **确保高度恒定**：所有导航项始终同时显示图标+文字，消除高度差
- **保持高亮效果**：选中项标题使用主题强调色，未选中项使用次要颜色

## Impact
- Affected specs: 底部导航栏UI组件（高度稳定性）
- Affected code:
  - `app/src/main/res/layout/activity_main.xml`
  - `app/src/main/java/io/legado/app/lib/theme/view/ThemeBottomNavigationVIew.kt`

## ADDED Requirements

### Requirement: 导航栏高度恒定不变
系统 SHALL 确保底部导航栏在任何选择状态下保持相同的高度，不因标签显示/隐藏而产生跳动。

#### Scenario: 切换导航项时高度稳定
- **WHEN** 用户点击底部导航栏的任意选项进行切换
- **THEN** 导航栏的整体高度应保持恒定（无任何跳动或抖动）
- **AND** 所有导航项均同时显示图标和文字标签

#### Scenario: 初始加载时高度稳定
- **WHEN** 应用启动并首次渲染底部导航栏
- **THEN** 导航栏立即以最终高度显示，无延迟或二次布局变化

### Requirement: 选中项高亮显示
系统 SHALL 在保持高度恒定的前提下，对选中的导航项应用高亮样式。

#### Scenario: 选中状态高亮
- **WHEN** 某个导航项处于选中状态
- **THEN** 该项的图标和文字均使用主题强调色（Accent Color）显示
- **AND** 视觉上明显区别于未选中项

#### Scenario: 未选中项默认样式
- **WHEN** 导航项处于未选中状态
- **THEN** 该项的图标和文字使用次要文字颜色（Secondary Text）
- **AND** 样式简洁但不影响整体布局稳定性

### Requirement: 紧凑布局适配
系统 SHALL 在所有项都显示标签的情况下，通过缩小尺寸保持导航栏紧凑。

#### Scenario: 图标尺寸优化
- **WHEN** 导航栏渲染4个带标签的导航项
- **THEN** 图标尺寸应为 20dp（默认24dp偏大）

#### Scenario: 文字尺寸优化
- **WHEN** 导航项显示标签文字
- **THEN** 选中项文字 11sp，未选中项 10sp
- **AND** 文字单行显示，不换行或截断

## MODIFIED Requirements

### Requirement: labelVisibilityMode 配置
将 `activity_main.xml` 中的 `labelVisibilityMode` 从 `"selected"` 改为 `"labeled"`，确保所有项始终显示标签。

| 模式 | 选中项 | 未选中项 | 高度稳定性 |
|------|--------|----------|------------|
| `unlabeled` (原始) | 仅图标 | 仅图标 | ✅ 稳定但无高亮 |
| `selected` (当前) | 图标+文字 | 仅图标 | ❌ 跳动 |
| **`labeled` (目标)** | 图标+文字 | 图标+文字 | ✅ 稳定+高亮 |

### Requirement: ThemeBottomNavigationVIew 初始化增强
在 init 块中增加以下属性设置：
- `itemIconSize = 20.dp` - 缩小图标
- 通过 `itemTextAppearanceActive` 和 `itemTextAppearanceInactive` 控制字体大小

## REMOVED Requirements
无