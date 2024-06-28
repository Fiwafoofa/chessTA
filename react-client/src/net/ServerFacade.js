import { HttpCommunicator } from "./HttpCommunicator";


export class ServerFacade {

  #serverUrl = "http://localhost:8080";
  #httpCommunicator = new HttpCommunicator();
  #authToken = "";

  #gameIdMappings = {};

  /**
   * 
   * @param {string} username 
   * @param {string} password 
   */
  async doLogin(username, password) {
    const jsonBody = {
      "username": username,
      "password": password
    };
    const authData = await this.#httpCommunicator.makeRequest("POST", this.#serverUrl+"/session", jsonBody, null);
    this.#authToken = authData["authToken"];
  }

  /**
   * 
   * @param {string} username 
   * @param {string} password 
   * @param {string} email 
   */
  async doRegister(username, password, email) {
    const jsonBody = {
      "username": username,
      "password": password,
      "email": email
    };
    const authData = await this.#httpCommunicator.makeRequest("POST", this.#serverUrl+"/user", jsonBody, null);
    this.#authToken = authData["authToken"];
  }

  async doLogout() {
    await this.#httpCommunicator.makeRequest("DELETE", this.#serverUrl+"/session", null, this.#authToken);
    this.#authToken = null;
  }

  async doListGames() {
    const gamesListJsonObj = await this.#httpCommunicator.makeRequest("GET", this.#serverUrl+"/game", null, this.#authToken); 
    let counter = 1;
    for (const gameElem of gamesListJsonObj["games"]) {
      this.#gameIdMappings[counter] = gameElem["gameID"];
      gameElem["gameID"] = counter;
      counter++;
    }
    gamesListJsonObj["games"].sort((gameOne, gameTwo) => gameOne["gameName"] < gameTwo["gameName"]);
    return gamesListJsonObj["games"];
  }

  /**
   * 
   * @param {string} gameName 
   */
  async doCreateGame(gameName) {
    const jsonBody = {
      "gameName": gameName
    }
    await this.#httpCommunicator.makeRequest("POST", this.#serverUrl+"/game", jsonBody, this.#authToken);
  }

  /**
   * 
   * @param {number} gameID 
   * @param {string} teamColor 
   */
  async doJoinGame(gameID, teamColor) {
    const realGameID = this.#gameIdMappings[gameID];
    const jsonBody = {
      "playerColor": teamColor,
      "gameID": realGameID
    }
    await this.#httpCommunicator.makeRequest("PUT", this.#serverUrl+"/game", jsonBody, this.#authToken);
  }

  /**
   * 
   * @param {number} gameID 
   */
  async doObserveGame(gameID) {

  }
}