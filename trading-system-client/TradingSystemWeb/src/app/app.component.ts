import {Component, OnInit, Output} from '@angular/core';
import {UserService} from './services/user.service';
import {WebSocketAPI} from './shared/apis/webSocketApi.model';
import {WebSocketService} from './services/web-socket.service';
import {NotificationDto} from './shared/notification.model';
import {Toast, ToasterConfig, ToasterService} from 'angular2-toaster';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit{

  constructor(private userService: UserService, private webSocketService: WebSocketService,
              private toasterService: ToasterService) {}
  title = 'TradingSystemWeb';
  isLogging = false;
  messages: any;

  public config1: ToasterConfig = new ToasterConfig({
    positionClass: 'toast-top-right',
    animation: 'fade'
  });

  ngOnInit(): void {
    this.userService.userLogoutEvent.subscribe(
      sucLogout => {
        if (sucLogout){
          this.isLogging = false;
        }
      }
    );
    this.userService.userLoggingEvent.subscribe(
      sucLogging => {
        if (sucLogging){
          this.isLogging = sucLogging;
        }
      }
    );
    this.webSocketService
      .setWebSocketAPI(new WebSocketAPI(new AppComponent(this.userService, this.webSocketService, this.toasterService), this.userService));
    this.messages = [];
    this.connect();
  }

  connect() {
    this.webSocketService.getWebSocketAPI()._connect();
  }

  disconnect() {
    this.webSocketService.getWebSocketAPI()._disconnect();
  }

  sendMessage() {
    this.webSocketService.getWebSocketAPI()._send();
  }

  handleMessage(message: NotificationDto) {
    if (!this.messages){
      this.messages = [message];
    }else{
      this.messages.push(message);
    }
    console.log(message.style);
    const toast: Toast = {
      type: message.style,
      title: message.title,
      body: message.content,
      showCloseButton: true
    };
    this.toasterService.pop(toast);
  }

  dismiss(message: NotificationDto) {
    message.dismissed = true;
  }

}
