import React, { useEffect } from 'react';

export const Gameplay = () => {

  useEffect(() => {
    const wsConnection = new WebSocket('ws://localhost:8080/ws');

    // WebSocket event listeners
    wsConnection.onopen = () => {
      console.log('WebSocket connected');
      // You can send messages here if needed
      wsConnection.send(JSON.stringify({
        
      }))
      // ws.send('Hello Server!');
    };

    wsConnection.onmessage = (event) => {
      console.log('Message received:', event.data);
      // Handle incoming messages from server
    };

    wsConnection.onclose = () => {
      console.log('WebSocket disconnected');
      // Handle WebSocket close event
    };

    return () => {
      // Cleanup function
      wsConnection.close();
    };
  }, []);

  return (
    <div>
      <h1>Gameplay</h1>
    </div>
  )
}