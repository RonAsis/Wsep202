import {Component, OnInit} from '@angular/core';
import {UserService} from '../services/user.service';
import {WebSocketService} from '../services/web-socket.service';
import {ShareService} from '../services/share.service';

@Component({
  selector: 'app-logged-in-user',
  templateUrl: './logged-in-user.component.html',
  styleUrls: ['./logged-in-user.component.css']
})
export class LoggedInUserComponent implements OnInit {
  loadedFeature: string;

  constructor(private userService: UserService, private webSocketService: WebSocketService,
              private shareService: ShareService) {
    this.tera();
    this.webSocketService.getWebSocketAPI()._send();
  }

  tera() {
    this.loadedFeature = 'Products';
  }

  ngOnInit(): void {
    this.userService.logoutNoEvent.subscribe(() => this.tera());
    this.shareService.featureSelected.subscribe(feature => this.onNavigate(feature));
  }

  onNavigate(feature: string) {
    this.loadedFeature = feature;
  }


}
