export class HttpCommunicator {

  /**
   * 
   * @param {string} type 
   * @param {string} path 
   * @param {object} jsonObject 
   * @param {string | null} authToken 
   */
  async makeRequest(type, path, jsonBodyObject, authToken) {
    console.log(`SENDING: ${JSON.stringify(jsonBodyObject)}`);
    let response;
    if (type !== "GET" && jsonBodyObject) {
      response = await fetch(path, {
        method: type,
        headers: {
          "Authorization": authToken
        },
        body: JSON.stringify(jsonBodyObject)
      })
    } else {
      response = await fetch(path, {
        method: type,
        headers: {
          "Authorization": authToken
        }
      })
    }
    

    const jsonResObj = await response.json();
    console.log(`RESPONSE RECEIVED: ${JSON.stringify(jsonResObj)}`);
    if (!response.ok) {
      throw new Error(jsonResObj["message"]);
    }
    return jsonResObj;
  }

}