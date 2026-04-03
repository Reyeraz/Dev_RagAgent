import { useState, useEffect, useRef } from 'react'
import axios from 'axios'

function App() {
  // 状态管理
  const [sessions, setSessions] = useState([])
  const [currentSession, setCurrentSession] = useState(null)
  const [messages, setMessages] = useState([])
  const [inputValue, setInputValue] = useState('')
  const [loading, setLoading] = useState(false)
  const [initialLoading, setInitialLoading] = useState(true)
  const [uploadFile, setUploadFile] = useState(null)
  const [uploadStatus, setUploadStatus] = useState('')
  const [isUploading, setIsUploading] = useState(false)
  const [error, setError] = useState(null)
  const messagesEndRef = useRef(null)
  
  // 使用固定用户ID user1
  const userId = 'user1'
  
  // 添加错误处理
  useEffect(() => {
    const handleError = (e) => {
      console.error('全局错误:', e)
      setError('应用发生错误，请刷新页面重试')
    }
    
    window.addEventListener('error', handleError)
    return () => {
      window.removeEventListener('error', handleError)
    }
  }, [])

  // 获取会话列表
  const fetchSessions = async () => {
    try {
      console.log('开始获取会话列表...')
      const response = await axios.get(`/users/${userId}/session`)
      console.log('获取会话列表成功:', response.data)
      
      // 确保sessions是数组
      if (Array.isArray(response.data)) {
        setSessions(response.data)
      } else {
        console.error('会话列表格式错误:', response.data)
        setSessions([])
      }
    } catch (error) {
      console.error('获取会话列表失败:', error)
      console.error('错误详情:', error.response?.data || error.message)
      
      // 设置空会话列表，避免白屏
      setSessions([])
      
      // 只在开发环境显示详细错误
      if (process.env.NODE_ENV === 'development') {
        setError(`获取会话失败: ${error.response?.data || error.message}`)
      } else {
        setError('获取会话列表失败，请检查网络连接或后端服务')
      }
    } finally {
      // 无论成功失败，都结束初始加载状态
      setInitialLoading(false)
    }
  }

  // 获取会话历史消息
  const fetchHistoryMessages = async (sessionId) => {
    try {
      const response = await axios.get(`/users/${userId}/session/${sessionId}`)
      const messages = response.data || []
      
      // 确保messages是数组且每个消息都有必要的字段
      if (Array.isArray(messages)) {
        const processedMessages = messages.map(msg => ({
          id: msg.id || `msg-${Date.now()}-${Math.random().toString(36).substr(2, 9)}`,
          content: msg.content || '',
          type: msg.type || 'ASSISTANT',
          metadata: msg.metadata || msg.metaData || { timestamp: new Date().toISOString() }
        }))
        setMessages(processedMessages)
      } else {
        console.error('历史消息格式错误:', messages)
        setMessages([])
      }
    } catch (error) {
      console.error('获取历史消息失败:', error)
      console.error('错误详情:', error.response?.data || error.message)
      setError('获取历史消息失败，请检查后端服务是否运行')
      setMessages([])
    }
  }

  // 创建新会话
  const createNewSession = async () => {
    try {
      // 发送POST请求时，需要指定正确的Content-Type
      const response = await axios.post(`/users/${userId}/session`, null, {
        headers: {
          'Content-Type': 'application/json'
        }
      })
      
      const newSession = response.data
      console.log('创建会话成功，返回数据:', newSession)
      
      // 处理新会话数据，确保有必要的字段
      const processedSession = {
        id: newSession.id || `session-${Date.now()}`,
        sessionTitle: newSession.sessionTitle || '未命名会话',
        status: newSession.status || 'ACTIVE',
        updateTime: newSession.updateTime || new Date().toISOString()
      }
      
      setSessions(prev => [processedSession, ...prev])
      setCurrentSession(processedSession)
      setMessages([])
    } catch (error) {
      console.error('创建会话失败:', error)
      console.error('错误详情:', error.response?.data || error.message)
      
      // 只在开发环境显示详细错误
      if (process.env.NODE_ENV === 'development') {
        setError(`创建会话失败: ${error.response?.data || error.message}`)
      } else {
        setError('创建会话失败，请检查后端服务是否运行')
      }
    }
  }

  // 切换会话
  const switchSession = (session) => {
    setCurrentSession(session)
    fetchHistoryMessages(session.id)
  }

  // 发送消息
  const sendMessage = async () => {
    if (!inputValue.trim() || !currentSession || loading) return

    const messageContent = inputValue.trim()
    setInputValue('')

    // 添加用户消息到本地
    const userMessage = {
      content: messageContent,
      type: 'USER',
      metadata: { timestamp: new Date().toISOString() }
    }
    setMessages(prev => [...prev, userMessage])
    setLoading(true)

    try {
      // 使用fetch API处理流式响应
      const response = await fetch(`/users/${userId}/session/${currentSession.id}`, {
        method: 'POST',
        headers: {
          'Accept': 'text/event-stream',
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          content: messageContent,
          type: 'USER',
          metaData: {}
        })
      })

      if (!response.ok) {
        throw new Error(`请求失败: ${response.status} ${response.statusText}`)
      }

      if (!response.body) {
        throw new Error('没有收到流式响应')
      }

      const reader = response.body.getReader()
      const decoder = new TextDecoder()
      let assistantMessage = ''
      let isFirstChunk = true

      // 读取流数据
      while (true) {
        const { done, value } = await reader.read()
        
        if (done) {
          console.log('流结束')
          break
        }

        // 解码数据
        const chunk = decoder.decode(value, { stream: true })
        console.log('收到数据块:', JSON.stringify(chunk))
        
        // 处理数据块
        const lines = chunk.split(/\r?\n/)
        for (const line of lines) {
          if (line.trim()) {
            let data = line
            // 移除data:前缀
            if (data.startsWith('data:')) {
              data = data.substring(5).trim()
            }
            
            if (isFirstChunk) {
              setMessages(prev => [...prev, {
                content: data,
                type: 'ASSISTANT',
                metadata: { timestamp: new Date().toISOString() }
              }])
              isFirstChunk = false
              assistantMessage = data
            } else {
              assistantMessage += data
              setMessages(prev => {
                const updated = [...prev]
                updated[updated.length - 1] = {
                  ...updated[updated.length - 1],
                  content: assistantMessage
                }
                return updated
              })
            }
          }
        }
      }

      // 释放锁
      reader.releaseLock()
      console.log('释放流锁')
    } catch (error) {
      console.error('发送消息失败:', error)
      
      // 构建友好的错误消息
      let errorMsg = ''
      if (error.name === 'AbortError') {
        errorMsg = '请求超时，请检查网络连接或稍后重试'
      } else if (error.message === 'Network Error') {
        errorMsg = '网络连接异常，请检查您的网络设置'
      } else {
        errorMsg = `发送消息失败: ${error.message || '未知错误'}`
      }
      
      // 添加错误消息
      setMessages(prev => [...prev, {
        content: errorMsg,
        type: 'ASSISTANT',
        metadata: { timestamp: new Date().toISOString() }
      }])
    } finally {
      setLoading(false)
    }
  }

  // 文件上传处理
  const handleFileChange = (e) => {
    const file = e.target.files[0]
    if (file) {
      setUploadFile(file)
      setUploadStatus(`已选择文件: ${file.name}`)
    }
  }

  // 上传文件到RAG系统
  const uploadToRAG = async () => {
    if (!uploadFile) {
      setUploadStatus('请先选择文件')
      return
    }

    setIsUploading(true)
    setUploadStatus('正在上传文件...')

    try {
      const formData = new FormData()
      formData.append('files', uploadFile)

      const response = await axios.post('/RAG/upload', formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        },
        onUploadProgress: (progressEvent) => {
          const percentCompleted = Math.round((progressEvent.loaded * 100) / progressEvent.total)
          setUploadStatus(`上传进度: ${percentCompleted}%`)
        }
      })

      setUploadStatus('文件上传成功！已添加到知识库')
      setUploadFile(null)
      // 清空文件输入
      document.getElementById('file-upload').value = ''
    } catch (error) {
      console.error('文件上传失败:', error)
      setUploadStatus('文件上传失败，请重试。')
    } finally {
      setIsUploading(false)
    }
  }

  // 自动滚动到底部
  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' })
  }, [messages])

  // 初始化获取会话列表
  useEffect(() => {
    const loadSessions = async () => {
      try {
        // 先添加延迟，避免瞬间白屏
        await new Promise(resolve => setTimeout(resolve, 100))
        await fetchSessions()
      } catch (error) {
        console.error('初始化加载失败:', error)
        setError('初始化失败，请检查后端服务是否运行')
      }
    }
    
    loadSessions()
  }, [])

  // 监听Enter键发送消息，Shift+Enter换行
  const handleKeyPress = (e) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault()
      sendMessage()
    }
  }

  return (
    <div className="app">
      {error && (
        <div style={{
          position: 'fixed',
          top: 0,
          left: 0,
          right: 0,
          backgroundColor: '#dc3545',
          color: 'white',
          padding: '10px 20px',
          zIndex: 1000,
          textAlign: 'center'
        }}>
          {error}
          <button 
            onClick={() => setError(null)} 
            style={{
              marginLeft: '10px',
              backgroundColor: 'white',
              color: '#dc3545',
              border: 'none',
              padding: '5px 10px',
              borderRadius: '3px',
              cursor: 'pointer'
            }}
          >
            关闭
          </button>
        </div>
      )}
      
      {/* 初始加载状态 */}
      {initialLoading ? (
        <div style={{
          display: 'flex',
          justifyContent: 'center',
          alignItems: 'center',
          width: '100%',
          height: '100%',
          backgroundColor: '#fafafa',
          flexDirection: 'column',
          gap: '20px'
        }}>
          <div style={{
            fontSize: '48px',
            opacity: 0.5
          }}>💬</div>
          <div className="loading" style={{ fontSize: '18px' }}>
            RAG Agent 加载中...
          </div>
          <div style={{
            fontSize: '14px',
            color: '#666'
          }}>
            正在连接后端服务...
          </div>
        </div>
      ) : (
        <>
          {/* 侧边栏 */}
          <aside className="sidebar">
            <div className="sidebar-header">
              <h1>RAG Agent</h1>
              <button className="new-chat-btn" onClick={createNewSession}>
                新对话
              </button>
            </div>

            {/* 上传区域 */}
            <div className="upload-section">
              <h2>知识库上传</h2>
              <button 
                className="upload-btn" 
                onClick={uploadToRAG}
                disabled={isUploading || !uploadFile}
              >
                {isUploading ? '上传中...' : '上传文件'}
              </button>
              <input
                id="file-upload"
                type="file"
                className="upload-input"
                onChange={handleFileChange}
                disabled={isUploading}
                accept=".txt,.pdf,.doc,.docx,.md"
              />
              <div className="upload-status">{uploadStatus}</div>
            </div>

            {/* 会话列表 */}
            <div className="sessions-list">
              {sessions.map(session => (
                <div
                  key={session.id}
                  className={`session-item ${currentSession?.id === session.id ? 'active' : ''}`}
                  onClick={() => switchSession(session)}
                >
                  <div className="session-title">{session.sessionTitle || '未命名会话'}</div>
                  <div className="session-time">
                    {session.updateTime && session.updateTime.length > 0 ? new Date(session.updateTime).toLocaleString() : '刚刚'}
                  </div>
                </div>
              ))}
            </div>
          </aside>

          {/* 主聊天区域 */}
          <main className="chat-main">
            {currentSession ? (
              <>
                <div className="chat-header">
                  <h2>{currentSession.sessionTitle}</h2>
                  <button 
                    className="clear-btn" 
                    onClick={() => setMessages([])}
                  >
                    清空聊天
                  </button>
                </div>

                <div className="chat-messages">
                  {messages.map((message, index) => (
                    <div 
                      key={index} 
                      className={`message ${message.type === 'USER' ? 'user' : 'assistant'}`}
                    >
                      <div className="message-avatar">
                        {message.type === 'USER' ? 'U' : 'A'}
                      </div>
                      <div className="message-content">
                        <div>{message.content}</div>
                        <div className="message-time">
                          {new Date(message.metadata.timestamp).toLocaleTimeString()}
                        </div>
                      </div>
                    </div>
                  ))}
                  {loading && (
                    <div className="message assistant">
                      <div className="message-avatar">A</div>
                      <div className="message-content">
                        <div className="loading">正在思考...</div>
                      </div>
                    </div>
                  )}
                  <div ref={messagesEndRef} />
                </div>

                <div className="chat-input-area">
                  <div className="input-container">
                    <textarea
                      className="message-input"
                      placeholder="输入消息... (Shift+Enter 换行)"
                      value={inputValue}
                      onChange={(e) => setInputValue(e.target.value)}
                      onKeyPress={handleKeyPress}
                      disabled={loading}
                      rows={1}
                    />
                    <button
                      className="send-btn"
                      onClick={sendMessage}
                      disabled={!inputValue.trim() || loading}
                      title="发送消息 (Enter)"
                    >
                      ➤
                    </button>
                  </div>
                </div>
              </>
            ) : (
              <div className="empty-state">
                <div className="empty-state-icon">💬</div>
                <h2>欢迎使用 RAG Agent</h2>
                <p>请从左侧创建新对话或选择现有对话开始聊天</p>
                <p>您可以上传文档到知识库，让AI根据文档内容进行回答</p>
              </div>
            )}
          </main>
        </>
      )}
    </div>
  )
}

export default App