import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {UserService} from '../../services/user.service';

@Component({
  selector: 'app-logout',
  templateUrl: './logout.component.html',
  styleUrls: ['./logout.component.css']
})
export class LogoutComponent implements OnInit {

  constructor(private userService: UserService) {}

  ngOnInit(): void {
  }

  onYes() {
    this.userService.logout();
  }

  onNo() {
    this.userService.logoutNoEvent.emit(false);
  }
}
