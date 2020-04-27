import {Component, ElementRef, OnInit, Output, ViewChild} from '@angular/core';
import {UserService} from '../../services/user.service';
import {first} from 'rxjs/operators';
import {Router} from '@angular/router';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent implements OnInit {
  sucRegister = 'the registration is succeed';
  failRegister = 'the registration is failed';
  @ViewChild('usernameInput', {static: false}) usernameInputRef: ElementRef;
  @ViewChild('firstNameInput', {static: false}) firstNameInputRef: ElementRef;
  @ViewChild('lastNameInput', {static: false}) lastNameInputRef: ElementRef;
  @ViewChild('passwordInput', {static: false}) passwordInputRef: ElementRef;
  messageReg: string;

  constructor(private userSystemService: UserService,
              private router: Router ) {
    this.messageReg = '';
  }

  ngOnInit(): void {
  }

  onRegisterUser() {
    console.log('registering');

    this.userSystemService.register(this.usernameInputRef.nativeElement.value,
      this.passwordInputRef.nativeElement.value,
      this.firstNameInputRef.nativeElement.value,
      this.lastNameInputRef.nativeElement.value)
      .subscribe(
        respone =>
        this.handlerRegister(respone)
      );

  }


  handlerRegister(isRegisterSuc: boolean) {
    if (isRegisterSuc){
      this.messageReg = this.sucRegister;
    }else{
      this.messageReg = this.failRegister;
    }
  }

  onClearDetails() {
    this.usernameInputRef.nativeElement.value = '';
    this.firstNameInputRef.nativeElement.value = '';
    this.lastNameInputRef.nativeElement.value = '';
    this.passwordInputRef.nativeElement.value = '';
    this.messageReg = '';
  }
}
