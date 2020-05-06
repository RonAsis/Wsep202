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
  @ViewChild('usernameInput', {static: false}) usernameInputRef: ElementRef;
  @ViewChild('firstNameInput', {static: false}) firstNameInputRef: ElementRef;
  @ViewChild('lastNameInput', {static: false}) lastNameInputRef: ElementRef;
  @ViewChild('passwordInput', {static: false}) passwordInputRef: ElementRef;
  @ViewChild('passwordAuthenticationInput', {static: false}) passwordAuthenticationInput: ElementRef;
  @ViewChild('imageInput', {static: false}) imageInput: ElementRef;
  private imgUrl: any;
  private selectedFile: File;
  message: string;
  messageColor: string;

  constructor(private userService: UserService,
              private router: Router) {
    this.clearMessage();
  }

  ngOnInit(): void {
    this.userService.registerEvent.subscribe(response => {
        this.clearDetailsOfReg();
        console.log(response);
        if (response) {
          this.message = 'the registration is succeed';
          this.messageColor = 'blue';
        } else {
          this.errorMessage('the registration is failed');
        }
      }
    );
  }

  onRegisterUser() {
    if (this.passwordInputRef.nativeElement.value !== this.passwordAuthenticationInput.nativeElement.value){
      this.errorMessage('The passwords do not match');
    }else if (this.passwordInputRef.nativeElement.value.length < 8 || this.passwordInputRef.nativeElement.value.length > 16){
      this.errorMessage('The passwords must be between 8 to 16 chars');
    }else if (this.passwordInputRef.nativeElement.value.replace(/\s/g, '') === '' ||
      this.firstNameInputRef.nativeElement.value.replace(/\s/g, '') === '' ||
      this.firstNameInputRef.nativeElement.value.replace(/\s/g, '') === '' ||
      this.lastNameInputRef.nativeElement.value.replace(/\s/g, '') === '' ||
      this.passwordAuthenticationInput.nativeElement.value.replace(/\s/g, '') === ''){
      this.errorMessage('must to fill all the details');
    }else{
      this.userService.register(this.usernameInputRef.nativeElement.value.replace(/\s/g, '').toLowerCase(),
        this.passwordInputRef.nativeElement.value,
        this.firstNameInputRef.nativeElement.value,
        this.lastNameInputRef.nativeElement.value,
        this.selectedFile);
    }
  }

  errorMessage(message: string){
    this.message = message;
    this.messageColor = 'red';
  }

  onClearDetails() {
    this.clearDetailsOfReg();
    this.clearMessage();
  }

  private clearMessage() {
    this.message = '';
    this.messageColor = '';
  }

  private clearDetailsOfReg() {
    this.usernameInputRef.nativeElement.value = '';
    this.firstNameInputRef.nativeElement.value = '';
    this.lastNameInputRef.nativeElement.value = '';
    this.passwordInputRef.nativeElement.value = '';
    this.passwordAuthenticationInput.nativeElement.value = '';
    this.imageInput.nativeElement.value = null;
  }

  fileUpload() {
    if (this.imageInput.nativeElement !== null && this.imageInput.nativeElement !== undefined) {
      // Access the uploaded file through the ElementRef
      this.selectedFile = this.imageInput.nativeElement.files[0];
      const reader = new FileReader();
      reader.readAsDataURL(this.imageInput.nativeElement.files[0]);
      reader.onload = (ev => this.imgUrl = reader.result);
    }
  }
}
