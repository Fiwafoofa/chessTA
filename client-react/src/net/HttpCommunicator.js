export class HttpCommunicator {


    async makePostRequest(url, path, jsonObj, authToken) {
        const response = await fetch(url + path, {
           method: 'POST',
           headers: {
               'Content-Type': 'application/json',
               'Authorization': authToken
           }
           body: JSON.stringify(jsonObj);
        });

        if (!response.ok) {
            console.log('ERROR');
            throw new Error("FAILUTE" + response.status);
        }

        const data = await response.json();

    }

}