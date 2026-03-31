# RAG Agent Frontend

一个基于React的现代化聊天界面，支持与RAG（检索增强生成）系统交互，包括文件上传功能。

## 技术栈

- React 18
- Vite
- Axios
- CSS3

## 功能特性

- 📱 现代化响应式设计
- 💬 实时聊天功能
- 📋 多会话管理
- 📁 知识库文件上传
- ⚡ 流式响应支持
- 🎨 美观的UI设计

## 快速开始

### 1. 安装依赖

```bash
npm install
```

### 2. 启动开发服务器

```bash
npm run dev
```

应用将在 `http://localhost:3000` 启动。

### 3. 构建生产版本

```bash
npm run build
```

构建产物将输出到 `dist` 目录。

## 配置说明

### 代理设置

在 `vite.config.js` 中可以配置后端API代理：

```javascript
server: {
  port: 3000,
  proxy: {
    '/api': {
      target: 'http://localhost:8080',
      changeOrigin: true
    }
  }
}
```

### API端点

应用默认调用以下API端点：

- `GET /api/sessions?userId={userId}` - 获取会话列表
- `POST /api/sessions` - 创建新会话
- `GET /api/messages/history?sessionId={sessionId}&userId={userId}` - 获取历史消息
- `POST /api/messages/send?sessionId={sessionId}` - 发送消息
- `POST /api/rag/upload` - 上传文件到知识库

## 使用说明

### 1. 创建新对话

点击侧边栏顶部的"新对话"按钮，即可创建一个新的聊天会话。

### 2. 发送消息

在输入框中输入消息，按Enter键发送，Shift+Enter换行。

### 3. 切换会话

在侧边栏的会话列表中点击任意会话，即可切换到该会话。

### 4. 上传文件到知识库

1. 在侧边栏的"知识库上传"区域点击"选择文件"按钮
2. 选择要上传的文件（支持.txt, .pdf, .doc, .docx, .md格式）
3. 点击"上传文件"按钮
4. 等待上传完成，文件将被添加到知识库中

### 5. 清空聊天

点击聊天区域顶部的"清空聊天"按钮，即可清空当前会话的所有消息。

## 项目结构

```
frontend/
├── src/
│   ├── App.jsx          # 主应用组件
│   ├── index.jsx        # 应用入口
│   └── index.css        # 样式文件
├── index.html           # HTML模板
├── package.json         # 项目配置
├── vite.config.js       # Vite配置
└── README.md            # 项目说明
```

## 浏览器支持

- Chrome (推荐)
- Firefox
- Safari
- Edge

## 许可证

MIT