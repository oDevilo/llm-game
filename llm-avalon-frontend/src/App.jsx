import React, { useState, useEffect } from 'react';
import axios from 'axios';

function App() {
  const [data, setData] = useState([]);
  const [message, setMessage] = useState('');
  const [wsData, setWsData] = useState([]);

  // HTTP请求示例
  const fetchData = async () => {
    try {
      // 这里需要替换为实际的后端API地址
      const response = await axios.get('http://localhost:8080/api/data');
      setData(response.data);
    } catch (error) {
      console.error('Error fetching data:', error);
    }
  };

  // WebSocket连接示例
  useEffect(() => {
    // 这里需要替换为实际的WebSocket地址
    const ws = new WebSocket('ws://localhost:8080/ws');
    
    ws.onopen = () => {
      console.log('WebSocket连接已建立');
    };
    
    ws.onmessage = (event) => {
      const newData = JSON.parse(event.data);
      setWsData(prev => [...prev, newData]);
    };
    
    ws.onclose = () => {
      console.log('WebSocket连接已关闭');
    };
    
    return () => {
      ws.close();
    };
  }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      // 这里需要替换为实际的后端API地址
      await axios.post('http://localhost:8080/api/message', { message });
      setMessage('');
    } catch (error) {
      console.error('Error sending message:', error);
    }
  };

  return (
    <div className="app">
      <h1>前端项目示例</h1>
      
      <div className="http-section">
        <h2>HTTP请求</h2>
        <button onClick={fetchData}>获取数据</button>
        <ul>
          {data.map((item, index) => (
            <li key={index}>{JSON.stringify(item)}</li>
          ))}
        </ul>
      </div>
      
      <div className="websocket-section">
        <h2>WebSocket推送数据</h2>
        <ul>
          {wsData.map((item, index) => (
            <li key={index}>{JSON.stringify(item)}</li>
          ))}
        </ul>
      </div>
      
      <div className="form-section">
        <h2>发送消息</h2>
        <form onSubmit={handleSubmit}>
          <input 
            type="text" 
            value={message} 
            onChange={(e) => setMessage(e.target.value)} 
            placeholder="输入消息"
          />
          <button type="submit">发送</button>
        </form>
      </div>
    </div>
  );
}

export default App;