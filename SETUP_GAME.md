# Queen's Blood 游戏在线运行设置指南

## 方案 1: 使用 Replit（推荐，最简单）

### 步骤：
1. 访问 [Replit](https://replit.com) 并注册/登录
2. 点击 "Create app"（创建应用）
3. 选择 "Java" 模板或搜索 "Java"
4. 将你的 Queen's Blood 项目代码上传或通过 Git 导入：
   ```bash
   git clone https://github.com/youning62/Queens-Blood.git
   ```
5. 在 Replit 中运行项目
6. 复制你的 Replit 项目 URL（格式：`https://replit.com/@你的用户名/项目名`）
7. 更新 `queens-blood.html` 中的 Replit URL

### 更新 queens-blood.html：
找到这一行并替换为你的实际 Replit URL：
```javascript
const replitUrl = 'https://replit.com/@你的用户名/项目名?embed=true';
```

**注意**：项目名中的空格会被替换为连字符（如 `Queen-s-Blood`）

## 方案 2: 使用 GitHub Codespaces

### 启用 Codespaces 的步骤：

1. **打开你的 GitHub 仓库**（如 `https://github.com/youning62/Queens-Blood`）

2. **方法一：通过仓库页面直接创建**
   - 点击仓库页面右上角的绿色 "Code" 按钮
   - 在弹出的菜单中，点击 "Codespaces" 标签
   - 点击 "Create codespace on main"（或你的主分支名）
   - 首次使用会提示授权，按照提示完成授权即可

3. **方法二：在仓库设置中启用（可选）**
   - 进入仓库的 "Settings"（设置）
   - 在左侧菜单中找到 "Codespaces"
   - 确认 Codespaces 已启用（通常默认已启用）

4. **使用 Codespaces**
   - 创建 Codespaces 后，会自动打开一个在线 VS Code 环境
   - 在终端中运行你的 Java 程序：
     ```bash
     javac *.java  # 编译（如果需要）
     java Main     # 运行主程序
     ```

5. **更新 `queens-blood.html` 中的 Codespaces 链接**
   - 找到 Codespaces 选项的链接
   - 将仓库地址替换为你的实际仓库地址：
     ```html
     <a href="https://github.com/codespaces/new?repo=你的用户名/你的仓库名" ...>
     ```
   - 例如：`https://github.com/codespaces/new?repo=youning62/Queens-Blood`

### 注意事项：
- Codespaces 是 GitHub 的付费功能（个人账户有免费额度）
- 免费账户每月有 60 小时的免费使用时间
- 适合需要完整开发环境的场景

## 方案 3: 使用 CheerpJ（高级，性能最好）

### 步骤：
1. 安装 CheerpJ：
   ```bash
   npm install -g cheerpj
   ```

2. 编译你的 Java 项目：
   ```bash
   cheerpj-compile -j your-game.jar
   ```

3. 将编译后的文件上传到你的网站

4. 在 `queens-blood.html` 中添加 CheerpJ 加载代码

## 方案 4: 使用 Jdoodle 或其他在线 Java 运行环境

1. 访问 [Jdoodle](https://www.jdoodle.com/online-java-compiler/)
2. 将你的代码粘贴进去
3. 获取嵌入代码
4. 添加到 `queens-blood.html`

## 当前设置

目前游戏页面已经准备好，你只需要：
1. 在 Replit 上创建项目并导入代码
2. 更新 `queens-blood.html` 中的 Replit URL
3. 或者使用其他方案

## 快速测试

访问 `queens-blood.html` 页面，点击 "Play on Replit" 按钮即可！
