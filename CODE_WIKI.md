# Legado (开源阅读) - Code Wiki

> **项目名称**: Legado / 开源阅读
> **包名**: `io.legado.app`
> **语言**: Kotlin (主模块) / Java (部分库模块) / TypeScript + Vue.js (Web 前端)
> **最低 SDK**: 21 (Android 5.0) | **目标 SDK**: 36 | **编译 SDK**: 36
> **JVM 目标**: Java 17

---

## 目录

- [1. 项目概述](#1-项目概述)
- [2. 项目架构总览](#2-项目架构总览)
- [3. 模块结构](#3-模块结构)
  - [3.1 app 主模块](#31-app-主模块)
  - [3.2 modules:book 模块](#32-modulesbook-模块)
  - [3.3 modules:rhino 模块](#33-modulesrhino-模块)
  - [3.4 modules:web 模块](#34-modulesweb-模块)
- [4. 核心包详解](#4-核心包详解)
  - [4.1 data — 数据层](#41-data--数据层)
  - [4.2 model — 业务模型层](#42-model--业务模型层)
  - [4.3 help — 辅助工具层](#43-help--辅助工具层)
  - [4.4 ui — 界面层](#44-ui--界面层)
  - [4.5 service — 服务层](#45-service--服务层)
  - [4.6 api — 对外接口层](#46-api--对外接口层)
  - [4.7 web — 内嵌 Web 服务](#47-web--内嵌-web-服务)
  - [4.8 constant — 常量定义](#48-constant--常量定义)
  - [4.9 base — 基础类](#49-base--基础类)
  - [4.10 lib — 第三方封装库](#410-lib--第三方封装库)
- [5. 关键类与函数说明](#5-关键类与函数说明)
  - [5.1 应用入口 — App.kt](#51-应用入口--appkt)
  - [5.2 数据库 — AppDatabase.kt](#52-数据库--appdatabasekt)
  - [5.3 核心实体类](#53-核心实体类)
  - [5.4 阅读引擎 — ReadBook.kt](#54-阅读引擎--readbookkt)
  - [5.5 网络书籍 — WebBook.kt](#55-网络书籍--webbookkt)
  - [5.6 规则引擎 — AnalyzeRule.kt](#56-规则引擎--analyzerulekt)
  - [5.7 本地书籍 — LocalBook.kt](#57-本地书籍--localbookkt)
  - [5.8 配置管理 — AppConfig.kt](#58-配置管理--appconfigkt)
- [6. 依赖关系](#6-依赖关系)
  - [6.1 模块依赖图](#61-模块依赖图)
  - [6.2 核心第三方依赖](#62-核心第三方依赖)
- [7. 数据流与关键流程](#7-数据流与关键流程)
  - [7.1 网络书籍阅读流程](#71-网络书籍阅读流程)
  - [7.2 书源规则解析流程](#72-书源规则解析流程)
  - [7.3 Web API 请求流程](#73-web-api-请求流程)
- [8. 构建与运行](#8-构建与运行)
  - [8.1 环境要求](#81-环境要求)
  - [8.2 构建步骤](#82-构建步骤)
  - [8.3 签名配置](#83-签名配置)
  - [8.4 构建变体](#84-构建变体)
  - [8.5 Web 前端构建](#85-web-前端构建)
- [9. 数据库架构](#9-数据库架构)
- [10. 扩展机制](#10-扩展机制)
  - [10.1 书源规则体系](#101-书源规则体系)
  - [10.2 JS 脚本引擎](#102-js-脚本引擎)
  - [10.3 WebDAV 同步](#103-webdav-同步)

---

## 1. 项目概述

Legado（开源阅读）是一款免费开源的 Android 小说阅读器。其核心设计理念是**"软件不提供内容，用户自定义规则抓取"**——通过灵活的书源规则系统，用户可以自行配置抓取规则来获取任意网站的书籍内容。

### 核心功能

| 功能 | 说明 |
|------|------|
| 自定义书源 | 用户设置规则抓取网页数据，支持搜索、发现、详情、目录、正文全链路 |
| RSS 订阅 | 订阅任意内容源，阅读文章 |
| 本地阅读 | 支持 TXT、EPUB、PDF、MOBI、UMD 等格式 |
| 高度自定义阅读 | 字体、颜色、背景、行距、段距、简繁转换等 |
| 多种翻页模式 | 覆盖、仿真、滑动、滚动 |
| 替换净化 | 去除广告、替换内容 |
| WebDAV 同步 | 阅读进度、书架备份同步 |
| Web 服务 | 内嵌 HTTP 服务器，支持浏览器端操作 |
| 朗读 / TTS | 本地 TTS 和在线 TTS 朗读 |

---

## 2. 项目架构总览

```
┌─────────────────────────────────────────────────────────┐
│                      Legado App                         │
├─────────────────────────────────────────────────────────┤
│  UI 层 (ui/)                                            │
│  ┌──────────┬──────────┬──────────┬──────────────────┐  │
│  │ MainActivity │ ReadBookActivity │ BookSourceActivity │ ... │  │
│  └──────────┴──────────┴──────────┴──────────────────┘  │
│           │ ViewModel / Callback                         │
├─────────────────────────────────────────────────────────┤
│  业务模型层 (model/)                                     │
│  ┌──────────┬──────────┬──────────┬──────────────────┐  │
│  │ ReadBook │ WebBook  │ LocalBook│ AnalyzeRule      │  │
│  │ Download │ Debug    │ Rss      │ SearchModel      │  │
│  └──────────┴──────────┴──────────┴──────────────────┘  │
│           │                                              │
├─────────────────────────────────────────────────────────┤
│  辅助工具层 (help/)                                      │
│  ┌──────────┬──────────┬──────────┬──────────────────┐  │
│  │ AppConfig│ BookHelp │ HttpHelper│ SourceHelp      │  │
│  │ ReadBookConfig│ ContentProcessor│ WebDav       │  │
│  └──────────┴──────────┴──────────┴──────────────────┘  │
│           │                                              │
├─────────────────────────────────────────────────────────┤
│  数据层 (data/)                                          │
│  ┌──────────────────────────────────────────────────┐   │
│  │  AppDatabase (Room)                               │   │
│  │  ┌─────┬──────┬────────┬──────┬────────┬─────┐  │   │
│  │  │Book │Source│Chapter │Rss   │Replace │ ... │  │   │
│  │  │Dao  │Dao   │Dao     │Dao   │RuleDao │     │  │   │
│  │  └─────┴──────┴────────┴──────┴────────┴─────┘  │   │
│  └──────────────────────────────────────────────────┘   │
│           │                                              │
├─────────────────────────────────────────────────────────┤
│  服务层 (service/)         │  API 层 (api/)              │
│  ┌────────────────────┐   │  ┌────────────────────┐     │
│  │ WebService         │   │  │ ReaderProvider     │     │
│  │ AudioPlayService   │   │  │ BookController     │     │
│  │ CacheBookService   │   │  │ BookSourceController│    │
│  │ DownloadService    │   │  │ RssSourceController │    │
│  │ TTS/HttpReadAloud  │   │  │ ReplaceRuleController│   │
│  └────────────────────┘   │  └────────────────────┘     │
│                                                           │
├─────────────────────────────────────────────────────────┤
│  子模块                                                   │
│  ┌──────────────┬──────────────┬──────────────────────┐  │
│  │ modules:book │ modules:rhino│ modules:web          │  │
│  │ (EPUB/UMD)   │ (JS Engine)  │ (Vue.js Frontend)    │  │
│  └──────────────┴──────────────┴──────────────────────┘  │
└─────────────────────────────────────────────────────────┘
```

---

## 3. 模块结构

### 3.1 app 主模块

主应用模块，包含所有核心业务逻辑、UI 界面、数据层和服务层。

**包路径**: `io.legado.app`
**构建文件**: `app/build.gradle`

关键子包:

| 子包 | 职责 |
|------|------|
| `api/` | 对外暴露的 Content Provider 和 HTTP API 控制器 |
| `base/` | Activity/Fragment/Service/ViewModel 基类，通用适配器 |
| `constant/` | 全局常量、事件总线定义、偏好键名 |
| `data/` | Room 数据库、实体类、DAO 接口 |
| `exception/` | 自定义异常类 |
| `help/` | 辅助工具类（配置、网络、书籍处理、加密、图片等） |
| `lib/` | 第三方库封装（Cronet、WebDAV、权限、主题、ICU 等） |
| `model/` | 核心业务模型（阅读、下载、调试、规则解析、本地/网络书籍） |
| `receiver/` | 广播接收器（媒体按钮、网络变化、时间/电量） |
| `service/` | Android Service（Web 服务、音频播放、缓存、下载、朗读） |
| `ui/` | 所有界面 Activity/Fragment/Dialog/Adapter |
| `utils/` | 通用工具类 |
| `web/` | 内嵌 HTTP 服务器（NanoHTTPD） |

### 3.2 modules:book 模块

电子书格式解析库模块，提供 EPUB 和 UMD 格式的解析能力。

**包路径**: `me.ag2s`
**构建文件**: `modules/book/build.gradle`

| 子包 | 职责 |
|------|------|
| `epublib/` | EPUB 电子书解析与写入库 |
| `epublib/domain/` | EPUB 数据模型（EpubBook, Resource, Metadata 等） |
| `epublib/epub/` | EPUB 读写引擎（EpubReader, EpubWriter） |
| `umdlib/` | UMD 格式电子书解析库 |
| `umdlib/domain/` | UMD 数据模型（UmdBook, UmdHeader, UmdChapters 等） |

### 3.3 modules:rhino 模块

Mozilla Rhino JavaScript 引擎的 Android 封装模块，为书源规则中的 JS 脚本提供执行环境。

**包路径**: `com.script`
**构建文件**: `modules/rhino/build.gradle`

| 关键类 | 职责 |
|--------|------|
| `RhinoScriptEngine` | JS 脚本引擎核心，提供脚本编译和执行 |
| `RhinoTopLevel` | JS 顶层作用域 |
| `RhinoWrapFactory` | Java 对象包装工厂，控制 JS 访问 Java 类的权限 |
| `RhinoContextFactory` | JS 执行上下文工厂 |
| `JavaAdapter` | JS 中实现 Java 接口的适配器 |
| `ReadOnlyJavaObject` | 只读 Java 对象包装，防止 JS 修改敏感属性 |

### 3.4 modules:web 模块

基于 Vue.js + TypeScript 的 Web 前端模块，提供浏览器端的书架浏览和书源编辑功能。

**技术栈**: Vue 3 + TypeScript + Vite + Pinia
**构建文件**: `modules/web/package.json`

| 目录 | 职责 |
|------|------|
| `src/api/` | 与 Legado 后端 API 通信 |
| `src/components/` | Vue 组件（书架项、目录、源编辑器等） |
| `src/views/` | 页面视图（BookShelf, SourceEditor, BookChapter） |
| `src/store/` | Pinia 状态管理（bookStore, sourceStore, connectionStore） |
| `src/router/` | Vue Router 路由配置 |
| `src/config/` | 书源/RSS 源编辑表单配置 |

---

## 4. 核心包详解

### 4.1 data — 数据层

数据层使用 Room 持久化框架，定义了应用的全部本地存储结构。

#### 实体类 (entities/)

| 实体类 | 表名 | 说明 |
|--------|------|------|
| `Book` | books | 书籍信息（书名、作者、URL、阅读进度等） |
| `BookSource` | book_sources | 书源配置（URL、搜索规则、详情规则、目录规则、正文规则等） |
| `BookChapter` | — | 书籍章节（章节标题、URL、所属书籍） |
| `BookGroup` | book_groups | 书籍分组（全部、本地、音频等系统分组 + 自定义分组） |
| `Bookmark` | bookmarks | 书签 |
| `ReplaceRule` | replace_rules | 替换/净化规则 |
| `RssSource` | rssSources | RSS 订阅源 |
| `RssArticle` | rssArticles | RSS 文章 |
| `RssStar` | rssStars | RSS 收藏 |
| `SearchBook` | search_books | 搜索结果缓存 |
| `SearchKeyword` | search_keywords | 搜索关键词历史 |
| `Cookie` | cookies | 网络 Cookie 存储 |
| `Cache` | caches | 通用缓存 |
| `ReadRecord` | read_records | 阅读时长记录 |
| `HttpTTS` | httpTTS | 在线 TTS 引擎配置 |
| `TxtTocRule` | txtTocRules | TXT 目录识别规则 |
| `DictRule` | dictRules | 字典规则 |
| `RuleSub` | ruleSubs | 规则订阅 |
| `Server` | servers | WebDAV 服务器配置 |
| `KeyboardAssist` | keyboardAssists | 键盘辅助按键配置 |
| `BookSourcePart` | (View) | 书源部分字段视图 |
| `BookProgress` | — | 阅读进度（非持久化，用于同步） |

#### 规则实体 (entities/rule/)

| 规则类 | 说明 |
|--------|------|
| `SearchRule` | 搜索规则（搜索URL、结果列表规则等） |
| `ExploreRule` | 发现规则（分类URL、结果列表规则等） |
| `BookInfoRule` | 书籍详情规则（封面、简介、作者等） |
| `TocRule` | 目录规则（章节列表、分页等） |
| `ContentRule` | 正文规则（内容、下一页、替换等） |
| `ReviewRule` | 书评规则 |
| `ExploreKind` | 发现分类 |

#### DAO 接口 (dao/)

每个实体对应一个 DAO，提供 CRUD 操作。核心 DAO:

| DAO | 关键方法 |
|-----|----------|
| `BookDao` | `insert()`, `update()`, `delete()`, `getByTocUrl()`, `getReadBook()`, `observeAll()`, `observeByGroup()` |
| `BookSourceDao` | `getBookSource()`, `getEnabled()`, `observeAll()`, `insert()`, `delete()` |
| `BookChapterDao` | `getChapter()`, `getChapterCount()`, `insert()`, `delByBook()` |
| `RssSourceDao` | `getRssSource()`, `getEnabled()`, `observeAll()` |
| `ReplaceRuleDao` | `getEnabled()`, `insert()`, `delete()` |

### 4.2 model — 业务模型层

业务模型层是应用的核心逻辑层，采用 Kotlin `object` 单例模式。

| 模型类 | 职责 | 关键方法 |
|--------|------|----------|
| `ReadBook` | 阅读引擎，管理当前阅读状态、章节加载、翻页、进度保存 | `resetData()`, `loadContent()`, `moveToNextChapter()`, `moveToPrevPage()`, `saveRead()`, `syncProgress()` |
| `WebBook` | 网络书籍操作入口，封装搜索/发现/详情/目录/正文全链路 | `searchBookAwait()`, `exploreBookAwait()`, `getBookInfoAwait()`, `getChapterListAwait()`, `getContentAwait()` |
| `LocalBook` | 本地书籍解析入口，根据文件类型分派到具体解析器 | `getContent()`, `getChapterList()`, `getCover()`, `getBookInfo()` |
| `AnalyzeRule` | 规则解析引擎，支持 XPath/JSoup/JSONPath/Regex/JS | `getString()`, `getElements()`, `evalJS()`, `splitRule()` |
| `AnalyzeUrl` | URL 解析引擎，处理请求参数、Headers、JS 等 | `getStrResponseAwait()`, `getStrResponse()`, `evalJS()` |
| `Download` | 下载管理器 | `start()`, `stop()`, `remove()` |
| `Debug` | 书源调试 | `log()`, `clear()` |
| `CacheBook` | 书籍缓存管理 | `getOrCreate()`, `download()`, `downloadAwait()` |
| `CheckSource` | 书源校验 | `check()`, `checkAll()` |
| `ReadAloud` | 朗读控制 | `play()`, `pause()`, `stop()` |
| `ReadManga` | 漫画阅读 | `loadContent()`, `getNextPage()` |
| `AudioPlay` | 音频播放控制 | `play()`, `pause()`, `prev()`, `next()` |
| `BookCover` | 书籍封面管理 | `toString()` (初始化默认封面) |
| `ImageProvider` | 阅读页图片提供者 | `loadImage()`, `clear()` |
| `SharedJsScope` | 共享 JS 作用域 | — |
| `SearchModel` | 搜索模型 | `search()`, `cancelSearch()` |

#### 规则解析子模型 (model/analyzeRule/)

| 类 | 职责 |
|----|------|
| `AnalyzeRule` | 规则解析核心，根据规则前缀分派到不同解析器 |
| `AnalyzeByJSoup` | JSoup CSS 选择器解析 |
| `AnalyzeByXPath` | XPath 解析 |
| `AnalyzeByJSonPath` | JSONPath 解析 |
| `AnalyzeByRegex` | 正则表达式解析 |
| `RuleAnalyzer` | 规则字符串分析器，拆分多规则 |
| `AnalyzeUrl` | URL 构建与请求执行 |
| `CustomUrl` | 自定义 URL 处理 |
| `RuleData` / `RuleDataInterface` | 规则数据上下文接口 |

#### 本地书籍子模型 (model/localBook/)

| 类 | 职责 |
|----|------|
| `LocalBook` | 本地书籍入口，分派到具体格式解析器 |
| `BaseLocalBookParse` | 本地书籍解析基类 |
| `TextFile` | TXT 文件解析 |
| `EpubFile` | EPUB 文件解析 |
| `MobiFile` | MOBI 文件解析 |
| `PdfFile` | PDF 文件解析 |
| `UmdFile` | UMD 文件解析 |

#### 网络书籍子模型 (model/webBook/)

| 类 | 职责 |
|----|------|
| `WebBook` | 网络书籍操作入口 |
| `BookList` | 书籍列表解析（搜索结果/发现列表） |
| `BookInfo` | 书籍详情解析 |
| `BookChapterList` | 章节目录解析 |
| `BookContent` | 章节正文解析 |
| `SearchModel` | 多源并发搜索模型 |

### 4.3 help — 辅助工具层

#### 配置类 (help/config/)

| 类 | 职责 |
|----|------|
| `AppConfig` | 全局应用配置（主题、点击动作、Cronet、日志等），实现 `OnSharedPreferenceChangeListener` |
| `ReadBookConfig` | 阅读界面配置（字体、行距、页边距、翻页动画等） |
| `ReadTipConfig` | 阅读提示配置 |
| `SourceConfig` | 书源配置（当前启用的书源等） |
| `ThemeConfig` | 主题配置（颜色、日夜模式切换） |
| `LocalConfig` | 本地配置 |

#### 书籍辅助 (help/book/)

| 类 | 职责 |
|----|------|
| `BookHelp` | 书籍缓存管理（保存/读取/清除章节内容缓存） |
| `BookContent` | 书籍内容获取辅助 |
| `ContentProcessor` | 内容处理器（替换规则、简繁转换、排版处理） |
| `BookExtensions` | Book 实体扩展函数 |

#### 网络辅助 (help/http/)

| 类 | 职责 |
|----|------|
| `HttpHelper` | HTTP 请求辅助，提供全局 OkHttpClient |
| `CookieManager` | Cookie 管理 |
| `CookieStore` | Cookie 持久化存储 |
| `Cronet` | Chromium Cronet 网络引擎封装 |
| `DecompressInterceptor` | 响应解压拦截器 |
| `OkHttpExceptionInterceptor` | OkHttp 异常拦截器 |
| `SSLHelper` | SSL 配置辅助 |
| `StrResponse` | 字符串响应封装 |
| `BackstageWebView` | 后台 WebView（用于 JS 渲染页面） |

#### 其他辅助类

| 类 | 职责 |
|----|------|
| `AppWebDav` | WebDAV 同步（进度上传/下载、备份/恢复） |
| `Backup` / `Restore` | 数据备份与恢复 |
| `DefaultData` | 默认数据初始化 |
| `CrashHandler` | 全局崩溃处理 |
| `TTS` | TTS 引擎管理 |
| `MediaHelp` | 媒体播放辅助 |
| `PaintPool` | 画笔对象池 |
| `JsExtensions` | JS 扩展函数（供书源 JS 调用） |
| `SourceHelp` | 书源管理辅助 |
| `ContentProcessor` | 正文内容处理（替换净化、简繁转换） |
| `EventMessage` | LiveEventBus 事件消息定义 |

### 4.4 ui — 界面层

界面层采用 MVVM 架构，Activity/Fragment 负责视图，ViewModel 负责业务逻辑。

#### 主要界面

| 界面 | Activity/Fragment | 说明 |
|------|-------------------|------|
| 启动页 | `WelcomeActivity` | 应用启动入口，支持多图标启动 |
| 主界面 | `MainActivity` | 底部导航：书架/发现/我的 |
| 书架 | `BooksFragment` | 书籍列表（列表/网格视图） |
| 发现 | `ExploreFragment` | 书源发现页 |
| 我的 | `MyConfigFragment` | 设置入口 |
| 阅读页 | `ReadBookActivity` | 阅读界面核心，管理翻页/菜单/排版 |
| 漫画阅读 | `ReadMangaActivity` | 漫画/图片阅读 |
| 音频播放 | `AudioPlayActivity` | 有声书播放 |
| 书籍详情 | `BookInfoActivity` | 书籍信息展示 |
| 搜索 | `SearchActivity` | 多源并发搜索 |
| 书源管理 | `BookSourceActivity` | 书源列表/导入/导出 |
| 书源编辑 | `BookSourceEditActivity` | 书源规则编辑 |
| 书源调试 | `BookSourceDebugActivity` | 规则调试 |
| RSS 源管理 | `RssSourceActivity` | RSS 源列表 |
| RSS 阅读 | `ReadRssActivity` | RSS 文章阅读 |
| 替换规则 | `ReplaceRuleActivity` | 替换/净化规则管理 |
| 书签 | `AllBookmarkActivity` | 全部书签 |
| 缓存 | `CacheActivity` | 书籍缓存下载 |
| 关于 | `AboutActivity` | 关于页面 |
| 导入 | `ImportBookActivity` | 本地书籍导入 |
| 远程书籍 | `RemoteBookActivity` | WebDAV 远程书籍 |

#### UI 子包结构

```
ui/
├── about/           # 关于页面
├── association/     # 外部关联（导入、验证码、URL确认）
├── book/
│   ├── audio/       # 音频播放
│   ├── bookmark/    # 书签
│   ├── cache/       # 缓存
│   ├── changecover/ # 更换封面
│   ├── changesource/# 换源
│   ├── explore/     # 发现
│   ├── group/       # 分组
│   ├── import/      # 导入（本地/远程）
│   ├── info/        # 书籍详情
│   ├── manage/      # 书架管理
│   ├── manga/       # 漫画阅读
│   ├── read/        # 文字阅读（核心）
│   ├── search/      # 搜索
│   ├── source/      # 书源管理/编辑/调试
│   └── toc/         # 目录
├── browser/         # WebView 浏览器
├── config/          # 设置页面
├── dict/            # 字典
├── file/            # 文件管理
├── login/           # 书源登录
├── main/            # 主界面
├── qrscan/          # 二维码扫描
├── replace/         # 替换规则
├── rss/             # RSS 相关
├── welcome/         # 启动页
└── widget/          # 桌面小组件
```

### 4.5 service — 服务层

| 服务 | 职责 |
|------|------|
| `WebService` | 内嵌 Web 服务器，提供 HTTP API 和 WebSocket |
| `AudioPlayService` | 音频播放前台服务 |
| `CacheBookService` | 书籍缓存下载服务 |
| `DownloadService` | 文件下载服务 |
| `CheckSourceService` | 书源校验服务 |
| `ExportBookService` | 书籍导出服务 |
| `TTSReadAloudService` | 本地 TTS 朗读服务 |
| `HttpReadAloudService` | 在线 TTS 朗读服务 |
| `WebTileService` | 快速设置磁贴（Web 服务开关） |

### 4.6 api — 对外接口层

提供两种外部接口方式：

| 组件 | 方式 | 说明 |
|------|------|------|
| `ReaderProvider` | Content Provider | 供其他应用通过 ContentResolver 查询数据 |
| `BookController` | HTTP API | 书籍/书架/阅读进度/阅读配置的 REST 接口 |
| `BookSourceController` | HTTP API | 书源的 CRUD 接口 |
| `RssSourceController` | HTTP API | RSS 源的 CRUD 接口 |
| `ReplaceRuleController` | HTTP API | 替换规则的 CRUD 接口 |

### 4.7 web — 内嵌 Web 服务

基于 NanoHTTPD 实现的轻量级 HTTP 服务器，在 `WebService` 中启动。

| 类 | 职责 |
|----|------|
| `HttpServer` | HTTP 请求路由，将 GET/POST 请求分派到对应 Controller |
| `WebSocketServer` | WebSocket 服务，支持实时通信 |

API 端点示例:
- `GET /getBookshelf` — 获取书架
- `GET /getBookSource` — 获取书源
- `POST /saveBookSource` — 保存书源
- `GET /getBookContent` — 获取章节内容
- `POST /saveBookProgress` — 保存阅读进度

### 4.8 constant — 常量定义

| 文件 | 说明 |
|------|------|
| `AppConst` | 应用常量（通知渠道 ID、默认值等） |
| `AppLog` | 日志管理 |
| `AppPattern` | 正则表达式常量（JS 模式、XPath 模式等） |
| `BookSourceType` | 书源类型（文字/音频/图片） |
| `BookType` | 书籍类型标记 |
| `EventBus` | LiveEventBus 事件键名 |
| `IntentAction` | Intent Action 常量 |
| `NotificationId` | 通知 ID |
| `PageAnim` | 翻页动画类型（覆盖/仿真/滑动/滚动/无） |
| `PreferKey` | SharedPreferences 键名 |
| `SourceType` | 源类型 |
| `Status` | 状态常量 |
| `Theme` | 主题常量 |

### 4.9 base — 基础类

| 类 | 说明 |
|----|------|
| `BaseActivity` | Activity 基类 |
| `BaseFragment` | Fragment 基类 |
| `VMBaseActivity` | 带 ViewModel 的 Activity 基类 |
| `VMBaseFragment` | 带 ViewModel 的 Fragment 基类 |
| `BaseDialogFragment` | Dialog Fragment 基类 |
| `BasePrefDialogFragment` | 偏好设置 Dialog 基类 |
| `BaseService` | Service 基类 |
| `BaseViewModel` | ViewModel 基类 |
| `AppContextWrapper` | Context 包装器（处理字体/语言等） |
| `RecyclerAdapter` | RecyclerView 通用适配器 |
| `DiffRecyclerAdapter` | 基于 DiffUtil 的适配器 |
| `ItemViewHolder` | 通用 ViewHolder |

### 4.10 lib — 第三方封装库

| 子包 | 说明 |
|------|------|
| `aliyun/` | 阿里云相关封装 |
| `cronet/` | Chromium Cronet 网络引擎封装（拦截器、回调、加载器） |
| `dialogs/` | 对话框工具（AlertBuilder、选择器） |
| `icu4j/` | ICU4J 字符编码检测 |
| `mobi/` | MOBI/AZW3 格式解析（KF8/KF6/压缩解压） |
| `permission/` | Android 权限请求封装 |
| `prefs/` | 偏好设置 UI 组件 |
| `theme/` | 主题/颜色系统（ThemeStore、TintHelper） |
| `webdav/` | WebDAV 客户端封装 |

---

## 5. 关键类与函数说明

### 5.1 应用入口 — App.kt

`App` 类继承 `Application`，是应用启动入口。

**初始化流程** (`onCreate`):

1. 注册 `CrashHandler` 全局崩溃处理
2. 应用日夜主题 `applyDayNightInit()`
3. 注册 Activity 生命周期回调 `LifecycleHelp`
4. 注册偏好变更监听 `AppConfig`
5. 异步初始化:
   - 日志系统 `LogUtils.init()`
   - 预下载 Cronet SO 库
   - 创建通知渠道
   - 配置 LiveEventBus
   - 执行默认数据升级 `DefaultData.upVersion()`
   - 初始化应用冻结监控 `AppFreezeMonitor`
   - 设置 URL 流处理器
   - 安装 GMS TLS 提供者（低版本 Android TLS 1.3 支持）
   - 初始化 Rhino JS 引擎，注册 Java 类包装器
   - 初始化书籍封面
   - 清除过期缓存数据
   - 初始化简繁转换引擎
   - 调整书源排序
   - 同步 WebDAV 阅读进度

**Rhino 初始化** (`initRhino`):
- 注册 `BookSource`, `RssSource`, `HttpTTS` 为 `NativeBaseSource`（可扩展）
- 注册规则类为 `ReadOnlyJavaObject`（只读，防止 JS 修改）

### 5.2 数据库 — AppDatabase.kt

使用 Room 持久化框架，当前版本 **75**。

**核心配置**:
- 数据库名: `legado.db`
- 21 个实体表 + 1 个视图
- 从版本 43 起大量使用 `AutoMigration` 自动迁移
- `dbCallback` 在数据库打开时确保系统分组存在（全部/本地/音频/未分组/更新失败）

**全局访问**:
```kotlin
val appDb by lazy {
    Room.databaseBuilder(appCtx, AppDatabase::class.java, "legado.db")
        .fallbackToDestructiveMigrationFrom(false, 1, 2, 3, 4, 5, 6, 7, 8, 9)
        .addMigrations(*DatabaseMigrations.migrations)
        .allowMainThreadQueries()
        .addCallback(AppDatabase.dbCallback)
        .build()
}
```

### 5.3 核心实体类

#### Book

书籍实体，核心字段:

| 字段 | 类型 | 说明 |
|------|------|------|
| `bookUrl` | String | 书籍唯一标识（主键） |
| `tocUrl` | String | 目录页 URL |
| `origin` | String | 来源书源 URL |
| `name` | String | 书名 |
| `author` | String | 作者 |
| `coverUrl` | String | 封面 URL |
| `durChapterIndex` | Int | 当前阅读章节索引 |
| `durChapterPos` | Int | 当前阅读章节内位置 |
| `durChapterTitle` | String | 当前章节标题 |
| `totalChapterNum` | Int | 总章节数 |
| `lastCheckTime` | Long | 最后检查更新时间 |
| `type` | Int | 书籍类型位掩码（本地/音频/图片等） |

#### BookSource

书源实体，核心字段:

| 字段 | 类型 | 说明 |
|------|------|------|
| `bookSourceUrl` | String | 书源 URL（主键） |
| `bookSourceName` | String | 书源名称 |
| `bookSourceGroup` | String | 书源分组 |
| `searchUrl` | String | 搜索 URL 模板 |
| `ruleSearch` | SearchRule | 搜索规则 |
| `ruleExplore` | ExploreRule | 发现规则 |
| `ruleBookInfo` | BookInfoRule | 详情规则 |
| `ruleToc` | TocRule | 目录规则 |
| `ruleContent` | ContentRule | 正文规则 |
| `enabled` | Boolean | 是否启用 |
| `enabledExplore` | Boolean | 是否启用发现 |
| `loginUrl` | String | 登录 URL |
| `loginUi` | String | 登录界面配置 |
| `loginCheckJs` | String | 登录检测 JS |
| `customOrder` | Int | 自定义排序 |
| `concurrentRate` | String | 并发速率限制 |

### 5.4 阅读引擎 — ReadBook.kt

`ReadBook` 是全局单例对象，是整个阅读功能的核心调度器。

**核心状态**:

| 属性 | 说明 |
|------|------|
| `book` | 当前阅读的书籍 |
| `bookSource` | 当前使用的书源 |
| `curTextChapter` | 当前章节的排版结果 |
| `prevTextChapter` | 上一章排版结果 |
| `nextTextChapter` | 下一章排版结果 |
| `durChapterIndex` | 当前章节索引 |
| `durChapterPos` | 当前阅读位置 |
| `contentProcessor` | 内容处理器 |

**核心方法**:

| 方法 | 说明 |
|------|------|
| `resetData(book)` | 切换书籍，重置所有阅读状态 |
| `loadContent(index, resetPageOffset)` | 加载指定章节内容（含前后预加载） |
| `moveToNextPage()` / `moveToPrevPage()` | 翻页 |
| `moveToNextChapter()` / `moveToPrevChapter()` | 切换章节 |
| `skipToPage(index)` | 跳转到指定页 |
| `saveRead()` | 保存阅读进度到数据库 |
| `syncProgress()` | WebDAV 进度同步 |
| `uploadProgress()` | 上传进度到 WebDAV |
| `preDownload()` | 预下载后续章节 |
| `upToc()` | 检查并更新目录 |

**内容加载流程**:
1. `loadContent(index)` → 检查本地缓存 `BookHelp.getContent()`
2. 缓存命中 → 直接 `contentLoadFinish()`
3. 缓存未命中 → `download()` 从网络获取
4. 下载完成 → `ContentProcessor` 处理替换/净化
5. `ChapterProvider.getTextChapterAsync()` 排版
6. 排版完成 → 更新 `prevTextChapter`/`curTextChapter`/`nextTextChapter`
7. 回调 UI 刷新

### 5.5 网络书籍 — WebBook.kt

`WebBook` 是网络书籍操作的统一入口，封装了完整的网络书籍获取链路。

**核心方法链**:

```
搜索: searchBookAwait() → AnalyzeUrl → BookList.analyzeBookList()
发现: exploreBookAwait() → AnalyzeUrl → BookList.analyzeBookList()
详情: getBookInfoAwait() → AnalyzeUrl → BookInfo.analyzeBookInfo()
目录: getChapterListAwait() → AnalyzeUrl → BookChapterList.analyzeChapterList()
正文: getContentAwait() → AnalyzeUrl → BookContent.analyzeContent()
```

每个方法都遵循相同模式:
1. 构建 `AnalyzeUrl`（处理 URL 模板、参数、Headers）
2. 执行网络请求获取响应
3. 检测登录状态（`loginCheckJs`）
4. 检测重定向
5. 使用 `AnalyzeRule` 按规则解析响应内容

### 5.6 规则引擎 — AnalyzeRule.kt

`AnalyzeRule` 是整个书源规则系统的核心，负责根据规则字符串解析内容。

**规则前缀分派**:

| 前缀 | 解析器 | 说明 |
|------|--------|------|
| `@css:` / 无前缀 | `AnalyzeByJSoup` | CSS 选择器（默认） |
| `@xpath:` | `AnalyzeByXPath` | XPath 表达式 |
| `@json:` / `$.` | `AnalyzeByJSonPath` | JSONPath 表达式 |
| `@js:` / `<js>` | Rhino JS Engine | JavaScript 脚本 |
| `@regex:` | `AnalyzeByRegex` | 正则表达式 |

**核心方法**:

| 方法 | 说明 |
|------|------|
| `getString(rule)` | 按规则获取字符串结果 |
| `getStringList(rule)` | 按规则获取字符串列表 |
| `getElements(rule)` | 按规则获取元素列表 |
| `evalJS(js)` | 执行 JS 脚本 |
| `splitRule(ruleStr)` | 拆分复合规则（`&&`, `%%`, `\|\|` 连接） |

**规则连接符**:

| 连接符 | 说明 |
|--------|------|
| `&&` | 并集（合并多个规则结果） |
| `\|\|` | 或集（任一规则有结果即返回） |
| `%%` | 交替合并 |

### 5.7 本地书籍 — LocalBook.kt

`LocalBook` 根据文件扩展名分派到不同格式解析器:

| 格式 | 解析器 | 依赖模块 |
|------|--------|----------|
| `.txt` | `TextFile` | app 内置 |
| `.epub` | `EpubFile` | modules:book (epublib) |
| `.mobi` / `.azw3` | `MobiFile` | app 内置 (lib/mobi) |
| `.pdf` | `PdfFile` | Android PdfRenderer |
| `.umd` | `UmdFile` | modules:book (umdlib) |

### 5.8 配置管理 — AppConfig.kt

`AppConfig` 实现了 `SharedPreferences.OnSharedPreferenceChangeListener`，是应用配置的实时管理器。

**核心配置项**:

| 配置 | 说明 |
|------|------|
| `isCronet` | 是否使用 Cronet 网络引擎 |
| `userAgent` | 自定义 User-Agent |
| `isEInkMode` | 墨水屏模式 |
| `clickActionXX` | 九宫格点击动作配置 |
| `themeMode` | 主题模式（日间/夜间/跟随系统/墨水屏） |
| `optimizeRender` | 优化渲染模式 |
| `recordLog` | 记录日志 |
| `syncBookProgress` | 同步阅读进度 |

---

## 6. 依赖关系

### 6.1 模块依赖图

```
┌─────────────┐
│    app       │
│  (主模块)    │
└──┬──────┬───┘
   │      │
   ▼      ▼
┌──────┐ ┌───────┐
│ book │ │ rhino │
│(EPUB │ │ (JS   │
│ UMD) │ │引擎)  │
└──────┘ └───────┘

modules:web (独立前端项目，编译后放入 assets/web/)
```

### 6.2 核心第三方依赖

| 类别 | 库 | 版本 | 说明 |
|------|-----|------|------|
| **语言** | Kotlin | 2.3.0 | 主开发语言 |
| **异步** | kotlinx-coroutines | 1.10.2 | 协程框架 |
| **数据库** | Room | 2.7.1 | ORM 框架 |
| **网络** | OkHttp | 5.3.2 | HTTP 客户端 |
| **网络** | Chromium Cronet | — | 高性能网络引擎（可选） |
| **HTML 解析** | Jsoup | 1.16.2 | HTML 解析 |
| **XPath** | JsoupXpath | 2.5.3 | XPath 解析 |
| **JSON** | JsonPath | 2.10.0 | JSONPath 解析 |
| **JSON** | Gson | 2.13.2 | JSON 序列化 |
| **JS 引擎** | Mozilla Rhino | 1.8.1 | JavaScript 执行引擎 |
| **图片** | Glide | 5.0.5 | 图片加载与缓存 |
| **SVG** | AndroidSVG | 1.4 | SVG 渲染 |
| **Web 服务** | NanoHTTPD | 2.3.1 | 内嵌 HTTP 服务器 |
| **媒体** | ExoPlayer (Media3) | 1.8.0 | 音频播放 |
| **UI** | Material Components | 1.13.0 | Material Design 组件 |
| **UI** | AndroidX AppCompat | 1.7.1 | 兼容库 |
| **事件** | LiveEventBus | 1.8.14 | 事件总线 |
| **备份** | WebDAV (自封装) | — | WebDAV 客户端 |
| **压缩** | libarchive | 1.1.6 | 压缩/解压 |
| **文本** | Commons Text | 1.13.1 | 文本处理（转义等） |
| **Markdown** | Markwon | 4.6.2 | Markdown 渲染 |
| **简繁转换** | Quick Chinese Transfer | 0.2.16 | 简繁体转换 |
| **加密** | Hutool Crypto | 5.8.22 | 加解密工具 |
| **二维码** | ZXing Lite | 3.3.0 | 二维码扫描 |
| **颜色** | ColorPicker | 1.1.0 | 颜色选择器 |
| **分析** | Firebase Analytics/Perf | 33.2.0 (BOM) | 崩溃和性能统计 |
| **工具** | Splitties | 3.0.0 | Kotlin 工具库 |
| **兼容** | Desugar JDK NIO | 2.1.5 | Java 8+ API 脱糖 |

---

## 7. 数据流与关键流程

### 7.1 网络书籍阅读流程

```
用户点击书籍
    │
    ▼
ReadBookActivity 启动
    │
    ▼
ReadBook.resetData(book)
    │
    ├── 获取 BookSource → bookSource
    ├── 创建 ContentProcessor
    └── loadContent(resetPageOffset=true)
            │
            ├── 加载当前章节 (index = durChapterIndex)
            ├── 加载下一章 (index = durChapterIndex + 1)
            └── 加载上一章 (index = durChapterIndex - 1)
                    │
                    ▼
            BookHelp.getContent(book, chapter)
                    │
            ┌───────┴───────┐
            │ 缓存命中       │ 缓存未命中
            ▼               ▼
        直接返回内容    CacheBook.download()
                            │
                            ▼
                    WebBook.getContentAwait()
                            │
                            ├── AnalyzeUrl 构建请求
                            ├── 执行网络请求
                            ├── loginCheckJs 检测
                            └── BookContent.analyzeContent()
                                    │
                                    ▼
                            AnalyzeRule 解析正文
                                    │
                                    ▼
                            保存到本地缓存
                                    │
                                    ▼
                    contentLoadFinish()
                            │
                            ├── ContentProcessor 处理（替换/净化/简繁）
                            ├── ChapterProvider 排版
                            └── 更新 TextChapter → 回调 UI 刷新
```

### 7.2 书源规则解析流程

```
规则字符串 (如: "div.content@html")
    │
    ▼
AnalyzeRule.splitRule()  ── 拆分复合规则
    │
    ▼
根据前缀选择解析器:
    │
    ├── @css: / 无前缀 → AnalyzeByJSoup
    │       └── JSoup CSS 选择器 → Elements → 提取内容
    │
    ├── @xpath: → AnalyzeByXPath
    │       └── XPath 表达式 → Nodes → 提取内容
    │
    ├── @json: / $. → AnalyzeByJSonPath
    │       └── JSONPath 表达式 → Object → 提取内容
    │
    ├── @js: → RhinoScriptEngine
    │       └── 编译执行 JS → result → 提取内容
    │
    └── @regex: → AnalyzeByRegex
            └── 正则匹配 → Groups → 提取内容
```

### 7.3 Web API 请求流程

```
浏览器/外部应用
    │
    ▼
HTTP 请求 (GET/POST)
    │
    ▼
WebService (Android Service)
    │
    ▼
HttpServer.serve(session)  ── NanoHTTPD 路由
    │
    ├── GET /getBookshelf → BookController.bookshelf
    ├── GET /getBookSource → BookSourceController.getSource
    ├── POST /saveBookSource → BookSourceController.saveSource
    ├── GET /getBookContent → BookController.getBookContent
    ├── POST /saveBookProgress → BookController.saveBookProgress
    └── ... 更多端点
    │
    ▼
Controller → appDb (Room) / ReadBook (Model)
    │
    ▼
ReturnData (JSON) → HTTP Response
```

---

## 8. 构建与运行

### 8.1 环境要求

| 工具 | 版本要求 |
|------|----------|
| JDK | 17+ |
| Android SDK | compileSdk 36 |
| Gradle | 通过 Gradle Wrapper（版本由项目指定） |
| Android Studio | 推荐最新稳定版 |
| Node.js | 18+ (仅 Web 前端模块需要) |

### 8.2 构建步骤

```bash
# 1. 克隆项目
git clone https://github.com/gedoor/legado.git
cd legado

# 2. 构建 Debug APK
./gradlew assembleAppDebug

# 3. 构建 Release APK（需要签名配置）
./gradlew assembleAppRelease

# 4. 输出路径
# app/build/outputs/apk/app/debug/legado_app_debug_3.XX.XXXX.apk
# app/build/outputs/apk/app/release/legado_app_release_3.XX.XXXX.apk
```

### 8.3 签名配置

在 `local.properties` 或 `gradle.properties` 中添加:

```properties
RELEASE_STORE_FILE=/path/to/keystore.jks
RELEASE_STORE_PASSWORD=your_store_password
RELEASE_KEY_ALIAS=your_key_alias
RELEASE_KEY_PASSWORD=your_key_password
```

### 8.4 构建变体

| 变体 | ApplicationId | 说明 |
|------|---------------|------|
| `appDebug` | `io.legado.app.debug` | 调试版本，可同时安装 |
| `appRelease` | `io.legado.app.release` | 正式发布版本 |

版本号格式: `3.YY.MMDDHH`（基于构建时间自动生成）
版本号: `10000 + gitCommitCount`

### 8.5 Web 前端构建

```bash
cd modules/web

# 安装依赖
npm install

# 开发模式
npm run dev

# 构建生产版本
npm run build
```

构建产物放入 `app/src/main/assets/web/` 目录。

---

## 9. 数据库架构

**数据库名**: `legado.db`
**当前版本**: 75

### 表结构

| 表名 | 实体类 | 主要字段 |
|------|--------|----------|
| `books` | Book | bookUrl(PK), name, author, coverUrl, durChapterIndex, durChapterPos, origin, type |
| `book_sources` | BookSource | bookSourceUrl(PK), bookSourceName, bookSourceGroup, searchUrl, enabled, customOrder |
| `book_groups` | BookGroup | groupId(PK), groupName, order, show, enableRefresh |
| `chapters` | BookChapter | bookUrl+index(联合PK), title, url, isVolume, tag |
| `replace_rules` | ReplaceRule | id(PK), name, group, order, pattern, replacement, isEnabled |
| `rssSources` | RssSource | sourceUrl(PK), sourceName, sourceIcon, sourceGroup, enabled |
| `rssArticles` | RssArticle | origin+link(联合PK), title, description, pubDate, image |
| `bookmarks` | Bookmark | bookUrl+chapterIndex+charIndex(联合PK), bookName, content |
| `search_books` | SearchBook | bookUrl+origin(联合PK), name, author, coverUrl, intro |
| `search_keywords` | SearchKeyword | word(PK), usage |
| `cookies` | Cookie | url(PK), cookie |
| `caches` | Cache | key(PK), value, deadline |
| `read_records` | ReadRecord | bookName(PK), readTime, lastRead |
| `httpTTS` | HttpTTS | id(PK), name, url, header, loginUrl, loginUi |
| `txtTocRules` | TxtTocRule | id(PK), name, rule, serialNumber, enabled |
| `ruleSubs` | RuleSub | id(PK), name, url, type, customOrder |
| `dictRules` | DictRule | id(PK), name, urlRule, showRule, enabled |
| `rssStars` | RssStar | origin+guid(联合PK), title, description |
| `rssReadRecords` | RssReadRecord | record(PK) |
| `keyboardAssists` | KeyboardAssist | key(PK), type, value, serialNo |
| `servers` | Server | id(PK), name, url, type, config |

### 迁移策略

- 版本 1-9: 破坏性迁移 (`fallbackToDestructiveMigrationFrom`)
- 版本 10-42: 手动 Migration (`DatabaseMigrations.migrations`)
- 版本 43-75: Room AutoMigration（自动检测 Schema 变更）

---

## 10. 扩展机制

### 10.1 书源规则体系

书源规则是 Legado 最核心的扩展机制，用户通过定义规则来抓取任意网站的内容。

**规则类型层级**:

```
BookSource
├── SearchRule      搜索规则
│   ├── url         搜索 URL 模板
│   ├── bookList    搜索结果列表规则
│   ├── name        书名规则
│   ├── author      作者规则
│   └── coverUrl    封面规则
├── ExploreRule     发现规则
│   ├── url         发现 URL 模板
│   └── bookList    发现列表规则
├── BookInfoRule    详情规则
│   ├── coverUrl    封面规则
│   ├── intro       简介规则
│   └── tocUrl      目录页 URL 规则
├── TocRule         目录规则
│   ├── chapterList 章节列表规则
│   ├── chapterName 章节名规则
│   └── chapterUrl  章节 URL 规则
└── ContentRule     正文规则
    ├── content     正文内容规则
    ├── nextContentUrl  正文下一页规则
    ├── replaceRegex    替换正则
    └── imageStyle      图片样式
```

**规则语法**:

| 语法 | 说明 | 示例 |
|------|------|------|
| `@css:selector` | CSS 选择器 | `@css:div.content@html` |
| `@xpath:expression` | XPath | `@xpath://div[@class="content"]` |
| `@json:expression` | JSONPath | `@json:$.data.list` |
| `@js:code` | JavaScript | `@js:result.html()` |
| `@regex:pattern` | 正则表达式 | `@regex:content=(.*?)&` |
| `@text` | 纯文本 | `@text` |
| `@html` | HTML 内容 | `div.content@html` |
| `@attr:name` | 属性获取 | `a@href` |

### 10.2 JS 脚本引擎

Legado 使用 Mozilla Rhino 作为 JS 执行引擎，在书源规则中支持 JS 脚本。

**JS 环境内置对象**:

| 对象 | 说明 |
|------|------|
| `result` | 当前请求的响应内容 |
| `baseUrl` | 基础 URL |
| `java` | Java 类访问（受限制） |
| `source` | 当前书源对象 |
| `book` | 当前书籍对象 |
| `chapter` | 当前章节对象 |

**安全限制**:
- `RhinoClassShutter` 限制 JS 可访问的 Java 类
- `ReadOnlyJavaObject` 包装规则类，防止 JS 修改
- `NativeBaseSource` 为书源/RSS源提供扩展接口

### 10.3 WebDAV 同步

通过 `AppWebDav` 和 `lib/webdav/` 实现数据同步:

| 同步内容 | 说明 |
|----------|------|
| 阅读进度 | 自动上传/下载书籍阅读进度 |
| 书架备份 | 备份/恢复完整书架数据 |
| 书源备份 | 备份/恢复书源配置 |
| 替换规则 | 备份/恢复替换规则 |

**同步策略**:
- 阅读进度: 本地进度领先则上传，远程领先则提示下载
- 备份: 手动触发或自动定时备份

---

## 附录: 项目文件统计

| 类别 | 数量 |
|------|------|
| Kotlin 源文件 | ~400+ |
| Java 源文件 | ~80+ |
| Layout XML | ~150+ |
| 数据库版本 | 75 |
| 实体表 | 21 |
| DAO 接口 | 20 |
| Activity | ~40+ |
| Service | 9 |
| 第三方依赖 | 40+ |
