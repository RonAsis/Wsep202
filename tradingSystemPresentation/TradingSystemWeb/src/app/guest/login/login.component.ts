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
  message: string;
  messageColor: string;

  constructor(private userService: UserService) {
    this.clearMessage();
  }

  ngOnInit(): void {
    this.userService.userLoggingEvent.subscribe(response => {
      if (!response){
        this.errorMessage('password or username dont correct');
      }
    });
  }

  onLogin() {
    this.userService.login(
      this.usernameInputRef.nativeElement.value,
      this.passwordInputRef.nativeElement.value
    );
  }

  onClearDetails() {
    this.passwordInputRef.nativeElement.value = '';
    this.usernameInputRef.nativeElement.value = '';
  }

  private clearMessage() {
    this.message = '';
    this.messageColor = '';
  }

  errorMessage(message: string){
    this.message = message;
    this.messageColor = 'red';
  }

}
