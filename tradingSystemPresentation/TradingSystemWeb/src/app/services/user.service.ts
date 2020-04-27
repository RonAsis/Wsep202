import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {UserSystem} from '../shared/userSystem.model';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private readonly guestUrl: string;

  constructor(private http: HttpClient) {
    this.guestUrl = 'http://localhost:8080/guest';
  }

  public register(username: string,
                  password: string,
                  firstName: string,
                  lastName: string) {
    const urlRegister = `${this.guestUrl}/` +
      'register-user/' +
      `${username}/` +
      `${password}/` +
      `${firstName}/` +
      `${lastName}`;
    return this.http.post<boolean>(
      urlRegister, null);
  }

  login(username: string, password: string) {
    const urlLogin = `${this.guestUrl}/` +
      'login/' +
      `${username}/` +
      `${password}` ;
    return this.http.put<{key: string ; value: boolean}>(
      urlLogin, null);
  }
}
