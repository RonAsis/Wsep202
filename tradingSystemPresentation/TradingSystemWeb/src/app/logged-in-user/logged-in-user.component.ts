import {AfterViewChecked, Component, EventEmitter, OnInit, Output, ViewChild, ViewChildren} from '@angular/core';
import {LogoutComponent} from './logout/logout.component';
import {UserService} from '../services/user.service';

@Component({
  selector: 'app-logged-in-user',
  templateUrl: './logged-in-user.component.html',
  styleUrls: ['./logged-in-user.component.css']
})
export class LoggedInUserComponent implements OnInit, AfterViewChecked {
  loadedFeature: string;
  constructor(private userService: UserService) {
    this.tera();
  }
  tera(){
    this.loadedFeature = 'Products';
  }
  ngOnInit(): void {
    this.userService.logoutNoEvent.subscribe(() => this.tera());

  }

  onNavigate(feature: string) {
    this.loadedFeature = feature;
  }

  ngAfterViewChecked(): void {
  }

}
