import React, { useState, useEffect, useRef } from 'react';
import './NewsModal.css';

const NewsModal = ({ news, isOpen, onClose }) => {
  const [isSpeaking, setIsSpeaking] = useState(false);
  const [resumePosition, setResumePosition] = useState(0);
  const utteranceRef = useRef(null);

  useEffect(() => {
    if (isOpen) {
      // 음성 합성 설정
      utteranceRef.current = new SpeechSynthesisUtterance();
      utteranceRef.current.lang = "ko-KR";
      utteranceRef.current.volume = 1;
      utteranceRef.current.rate = 0.85;
      utteranceRef.current.pitch = 0.98;
    }

    // 컴포넌트 언마운트 시 음성 합성 종료
    return () => {
      if (isSpeaking) {
        stopSpeaking();
      }
    };
  }, [isOpen]);

  // 음성 합성 시작
  const startSpeaking = () => {
    const title = news.title;
    const content = news.content;
    const fullText = `${title} ${content}`;
    const resumedText = fullText.substring(resumePosition);

    let textChunks = splitTextIntoChunks(resumedText);
    speakChunks(textChunks);
  };

  // 음성 합성 종료
  const stopSpeaking = () => {
    if (utteranceRef.current) {
      window.speechSynthesis.cancel();
      setIsSpeaking(false);
      setResumePosition(utteranceRef.current.text.length);
    }
  };

  // 텍스트를 '.'과 ','을 기준으로 나누는 함수
  const splitTextIntoChunks = (text) => {
    let chunks = [];
    let startIndex = 0;
    let endIndex = 0;

    while (endIndex < text.length) {
      if (text[endIndex] === '. ' || text[endIndex] === ',') {
        const chunk = text.substring(startIndex, endIndex + 1).trim();
        chunks.push(chunk);
        startIndex = endIndex + 1;
      }
      endIndex++;
    }

    if (startIndex < endIndex) {
      const chunk = text.substring(startIndex).trim();
      chunks.push(chunk);
    }

    return chunks;
  };

  // 구간별로 읽기 함수
  const speakChunks = (chunks) => {
    if (chunks.length === 0) {
      setIsSpeaking(false);
      return;
    }

    let currentChunk = chunks.shift();

    utteranceRef.current.text = currentChunk;
    window.speechSynthesis.speak(utteranceRef.current);
    setIsSpeaking(true);

    utteranceRef.current.onend = () => {
      setTimeout(() => {
        speakChunks(chunks);
      }, 200);
    };
  };

  if (!isOpen) return null;

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content news-detail-modal" onClick={(e) => e.stopPropagation()}>
        <span className="close" onClick={onClose}>&times;</span>
        
        <div className="news-detail-container">
          <div className="p-2 text-center">
            <h1>{news.title}</h1>
          </div>
          <hr />
          <div className="news-content">
            <p>{news.content}</p>
            <hr />
            <p>{news.date}</p>
          </div>
        </div>
        
        <div className="news-controls">
          <button
            className="custom-btn btn-1"
            onClick={isSpeaking ? stopSpeaking : startSpeaking}
          >
            <span>{isSpeaking ? '멈추기' : '읽기'}</span>
          </button>
        </div>
      </div>
    </div>
  );
};

export default NewsModal; 