// 测试前端API调用的脚本
import axios from 'axios';

async function testAPIs() {
  try {
    console.log('测试API连接...');
    
    // 测试获取会话列表
    console.log('\n1. 测试获取会话列表:');
    try {
      const sessionsResponse = await axios.get('http://localhost:8080/users/user1/session');
      console.log('状态码:', sessionsResponse.status);
      console.log('响应数据:', sessionsResponse.data);
      console.log('响应数据类型:', typeof sessionsResponse.data);
      console.log('是否为数组:', Array.isArray(sessionsResponse.data));
    } catch (error) {
      console.error('获取会话列表失败:', error.message);
      if (error.response) {
        console.error('响应状态码:', error.response.status);
        console.error('响应数据:', error.response.data);
      }
    }
    
    // 测试创建会话
    console.log('\n2. 测试创建会话:');
    try {
      const createResponse = await axios.post('http://localhost:8080/users/user1/session');
      console.log('状态码:', createResponse.status);
      console.log('响应数据:', createResponse.data);
    } catch (error) {
      console.error('创建会话失败:', error.message);
      if (error.response) {
        console.error('响应状态码:', error.response.status);
        console.error('响应数据:', error.response.data);
      }
    }
    
  } catch (error) {
    console.error('测试脚本出错:', error);
  }
}

testAPIs();
