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
  messageLogin: string;

  constructor(private userService: UserService) {
    this.messageLogin = '';
  }

  ngOnInit(): void {
  }

  onLogin() {
    this.messageLogin = '';
    const succeed = this.userService.login(
      this.usernameInputRef.nativeElement.value,
      this.passwordInputRef.nativeElement.value
    );
    if (!succeed){
      this.onClearDetails();
      this.messageLogin = 'logging failed';
    }
  }

  onClearDetails() {
    this.passwordInputRef.nativeElement.value = '';
    this.usernameInputRef.nativeElement.value = '';
  }
}
