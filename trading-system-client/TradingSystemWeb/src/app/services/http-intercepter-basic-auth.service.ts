import { Injectable } from '@angular/core';
import {HttpInterceptor, HttpRequest, HttpHandler, HttpEvent, HttpHeaders} from '@angular/common/http';
import {ShareService} from './share.service';

@Injectable({
  providedIn: 'root'
})
export class HttpIntercepterBasicAuthService implements HttpInterceptor{

  constructor(private shareService: ShareService) { }
  intercept(req: HttpRequest<any>, next: HttpHandler){
    const username = this.shareService.username;
    const basicAuthenticationHttpHeader = this.shareService.basicAuthenticationHttpHeader;
    console.log(basicAuthenticationHttpHeader);
    if (username && basicAuthenticationHttpHeader && !req.url.includes('guest')){
      req = req.clone({
        setHeaders : {
          Authorization: basicAuthenticationHttpHeader
        }
      });
    }
    return next.handle(req);
  }
}
