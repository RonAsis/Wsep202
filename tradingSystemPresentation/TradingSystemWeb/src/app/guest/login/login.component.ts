import {Component, ElementRef, OnInit, Output, ViewChild} from '@angular/core';
import {UserService} from '../../services/user.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {
  @ViewChild('usernameInput', {static: false}) usernameInputRef: ElementRef;
  @ViewChild('passwordInput', {static: false}) passwordInputRef: ElementRef;

  @Output() isAdmin: boolean;
  @Output() uuid: string;
  constructor(private userService: UserService) {
    this.isAdmin = false;
  }

  ngOnInit(): void {
  }

  onLogin() {
    this.userService.login(
      this.usernameInputRef.nativeElement.value,
      this.passwordInputRef.nativeElement.value
    ).subscribe(
      response => console.log(response));
  }

  handlerLogin(pair: {key: string ; value: boolean}){
      this.isAdmin = pair.value;
      this.uuid = pair.key;
  }

  onClearDetails() {

  }
}
