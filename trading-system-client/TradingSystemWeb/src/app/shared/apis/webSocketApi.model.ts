import * as Stomp from 'stompjs';
import * as SockJS from 'sockjs-client';
import {UserService} from '../../services/user.service';
import {AppComponent} from '../../app.component';
import {NotificationDto} from '../notification.model';

export class WebSocketAPI {
  private readonly webSocketTradingSystemsSubscribeEndpoint = '/user/trading-system-client/notification';
  private readonly webSocketTradingSystemSendEndpoint = '/trading-system-server/connect-notification-system';
  webSocketEndPoint = 'http://localhost:8080/trading-system-web-socket';
  topic = '/user/trading-system-client/notification';
  stompClient: any;
  appComponent: AppComponent;
  constructor(appComponent: AppComponent, private userService: UserService){
    this.appComponent = appComponent;
  }
  _connect() {
    console.log('Initialize WebSocket Connection');
    const ws = new SockJS(this.webSocketEndPoint);
    this.stompClient = Stomp.over(ws);
    const thisRef = this;
    // tslint:disable-next-line:only-arrow-functions
    thisRef.stompClient.connect({},  (frame) => {
      thisRef.stompClient.subscribe(thisRef.topic, (sdkEvent) => {
        console.log(sdkEvent.body);
        thisRef.onMessageReceived(JSON.parse(sdkEvent.body));
      });
    }, this.errorCallBack);
  }

  _disconnect() {
    if (this.stompClient !== null) {
      this.stompClient.disconnect();
    }
    console.log('Disconnected');
  }

  // on error, schedule a reconnection attempt
  errorCallBack(error) {
    console.log('errorCallBack -> ' + error);
    setTimeout(() => {
      this._connect();
    }, 5000);
  }

  /**
   * Send message to sever via web socket
   */
  _send() {
    console.log('calling connect');
    this.stompClient.send('/trading-system-server/connect-notification-system', {},
      JSON.stringify({username: this.userService.getUsername(), uuid: this.userService.getUuid()}));
  }

  onMessageReceived(notification: NotificationDto) {
    console.log('Message Recieved from Server :: ' + notification.content);
    this.appComponent.handleMessage(notification);
  }
}
