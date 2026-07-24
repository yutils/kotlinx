# AGENTS.md — kotlinx 给 AI / 协作者的工作指南

> 本文面向 **AI 编程助手** 与后续接手的开发者。读完应能独立完成：在 `:kotlinx` 加扩展、在 `:test` 加用例、真机跑测验证。

---

## 1. 仓库是什么

| 项 | 说明 |
|----|------|
| 库名 | `com.kotlinx:kotlinx`（Maven Central） |
| 结构 | 双模块：`:kotlinx`（库）+ `:test`（Compose 分类测试 App） |
| 语言 | Kotlin，AndroidX，Compose Material |
| 通信 | 与用户对话默认用 **简体中文** |

初始化（使用库的 App）：

```kotlin
Kotlinx.init(application)
```

**注意**：本库不依赖 `yutils`。

---

## 2. 目录速查

```
kotlinx/src/main/java/com/kotlinx/
  Kotlinx.kt          # 全局 Application / Toast 持有
  extend/             # 扩展函数（String/File/Bitmap/Uri…）
  extend/view/        # View / RecyclerView / Adapter
  utils/              # TTS、ExternalFile、ui/io、debounce

test/src/main/java/com/kotlinx/test/
  MainActivity.kt     # LAUNCHER → Compose 测试台
  harness/            # TestCatalog / TestStore / CoverageGaps / CoverageStore
  ui/TestApp.kt       # 首页分类 + 分类详情 + 日志
  *DemoActivity.kt    # 手势 / Recycler / SAF 演示
```

**启动入口**：`com.kotlinx.test.MainActivity`。

---

## 3. 怎么新增库功能（`:kotlinx`）

1. 放对包：校验/变换进 `extend/`，View 相关进 `extend/view/`，需 Application 的进 `utils/` 或 `Kotlinx`。
2. 风格：Kotlin 扩展函数为主；类/文件头给简短用法注释。
3. 需要 Context 的 API：显式传参，或依赖 `Kotlinx.init` 后的 `Kotlinx.app`。
4. 改完必测：能 Auto 断言的写进 `TestCatalog`；必须交互的写 Manual 或演示页。
5. 同步 `CoverageGaps.coveredKeywords` / `intentionallySkipped`。
6. 最小改动；不主动 commit/push（除非用户明确要求）。

---

## 4. 测试台怎么工作

| 类型 | 判定 |
|------|------|
| AUTO | `run` 内 `assertTrue` / `assertEq` / `error`；抛异常 → FAIL |
| MANUAL | 点「执行/打开」后副作用；再点「通过」/「失败」 |

关键文件：

- `harness/TestCatalog.kt` — **全部用例目录**（漏写 = 界面看不到）
- `harness/TestStore.kt` — 跑测、日志、汇总
- `harness/TestCoverageStore.kt` — SharedPreferences 持久化状态
- `harness/CoverageGaps.kt` — 覆盖说明 + 刻意不测
- `ui/TestApp.kt` — 「一键跑全部 / 清空 / 说明 / 运行日志」

首页能力对齐 yutils：自动通过数、清空状态与日志、覆盖说明 Dialog、底部运行日志、重启保留 PASS/FAIL。

### 加一条 AUTO

在 `buildAllTestCases()` 里：

```kotlin
add(auto(TestCategory.JSON, "json.demo", "说明") {
    assertEq(1 + 1, 2)
})
```

### 加一条 MANUAL

```kotlin
add(manual(TestCategory.VIEW, "view.foo", "需人工确认的描述") { ctx ->
    // 可选副作用；也可为空，靠演示页
})
```

演示页跳转在 `TestApp.CategoryScreen` 的 `when (state.def.id)` 里登记。

---

## 5. 怎么跑

```text
./gradlew :test:installDebug
```

真机：打开 App → **一键跑全部自动项** → 看底部日志与「自动 x/y」。  
人工项进对应分类点「执行/打开」再点通过/失败。  
**清空**：清状态 + 日志（持久化一并清）。  
**说明**：刻意不测清单。

---

## 6. 刻意不测（摘要）

手势/软键盘/触感/TTS 听感、SAF 授权读写、`BaseAdapter` 网络分页、`RecyclerView` 滚动监听、权限 Launcher 等——见 `CoverageGaps.intentionallySkipped` 与已有 Manual/演示页。
