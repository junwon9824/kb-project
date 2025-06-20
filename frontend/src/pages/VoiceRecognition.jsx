import React, { useState, useEffect, useRef } from 'react';
import Header from '../components/Header';
import './VoiceRecognition.css';

const VoiceRecognition = () => {
  const [messages, setMessages] = useState([]);
  const [isListening, setIsListening] = useState(false);
  const [socket, setSocket] = useState(null);
  const [recognition, setRecognition] = useState(null);
  const chatContainerRef = useRef(null);

  useEffect(() => {
    // WebSocket 연결
    const ws = new WebSocket('ws://localhost:8080/socket');
    
    ws.onopen = () => {
      console.log('WebSocket connection established.');
      const welcomeMessage = "무엇을 도와드릴까요?";
      speakText(welcomeMessage);
      addMessage(welcomeMessage, 'server');
    };

    ws.onmessage = (event) => {
      const message = event.data;
      console.log('Received message:', message);
      addMessage(message, 'server');
      speakText(message);
    };

    ws.onclose = () => {
      console.log('WebSocket connection closed.');
    };

    ws.onerror = (event) => {
      console.error('WebSocket error:', event);
    };

    setSocket(ws);

    // 컴포넌트 언마운트 시 WebSocket 연결 해제
    return () => {
      ws.close();
    };
  }, []);

  // 메시지 추가
  const addMessage = (message, sender) => {
    setMessages(prev => [...prev, { text: message, sender, id: Date.now() }]);
  };

  // 채팅 컨테이너 스크롤을 맨 아래로
  useEffect(() => {
    if (chatContainerRef.current) {
      chatContainerRef.current.scrollTop = chatContainerRef.current.scrollHeight;
    }
  }, [messages]);

  // 음성 인식 시작
  const startRecognition = () => {
    if (!('webkitSpeechRecognition' in window)) {
      alert('이 브라우저는 음성 인식을 지원하지 않습니다.');
      return;
    }

    const recognitionInstance = new window.webkitSpeechRecognition();
    recognitionInstance.lang = 'ko-KR';
    recognitionInstance.continuous = true;

    recognitionInstance.onresult = (event) => {
      const transcript = event.results[0][0].transcript;
      console.log(transcript);
      addMessage(transcript, 'user');
      processMessage(transcript);
    };

    recognitionInstance.onerror = (event) => {
      console.error('음성 인식 오류:', event.error);
      setIsListening(false);
    };

    recognitionInstance.onend = () => {
      setIsListening(false);
    };

    recognitionInstance.start();
    setRecognition(recognitionInstance);
    setIsListening(true);
  };

  // 음성 인식 중지
  const stopRecognition = () => {
    if (recognition) {
      recognition.stop();
      setIsListening(false);
    }
  };

  // 메시지 처리 및 서버로 전송
  const processMessage = (message) => {
    if (socket && socket.readyState === WebSocket.OPEN) {
      socket.send(message);
    }
  };

  // 텍스트를 음성으로 변환
  const speakText = (text) => {
    if ('speechSynthesis' in window) {
      const msg = new SpeechSynthesisUtterance();
      msg.text = text;
      msg.lang = 'ko-KR';
      msg.rate = 0.9;
      window.speechSynthesis.speak(msg);
    }
  };

  return (
    <>
      <Header />
      <div className="voice-recognition-container">
        <h1>음성 인식</h1>
        
        <div className="result-container">
          <div id="result"></div>
        </div>
        
        <h1>채팅</h1>
        <div className="chat-container" ref={chatContainerRef}>
          {messages.map((message) => (
            <div
              key={message.id}
              className={`message ${message.sender === 'user' ? 'userMessageContainer' : 'serverMessageContainer'}`}
            >
              <div className={message.sender === 'user' ? 'userMessage' : 'serverMessage'}>
                {message.text}
              </div>
            </div>
          ))}
        </div>

        <div className="buttons-container">
          <button
            className={`btn btn-primary btn-lg ${isListening ? 'listening' : ''}`}
            onClick={isListening ? stopRecognition : startRecognition}
          >
            {isListening ? 'Stop' : 'Start'}
          </button>
        </div>
      </div>
    </>
  );
};

export default VoiceRecognition; 