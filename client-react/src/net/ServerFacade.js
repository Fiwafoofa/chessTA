export class ServerFacade {

    #url = "http://localhost:8080";
    #token = "";

    loginUser(username, password) {
        console.log(`${username} ${password}`);
    }

    registerUser(username, password, email) {
        console.log(`${username} ${password} ${email}`);
    }

}