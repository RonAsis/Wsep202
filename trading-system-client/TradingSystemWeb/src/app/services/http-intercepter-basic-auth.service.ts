import { Injectable } from '@angular/core';
import {
  HttpInterceptor,
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpHeaders,
  HttpErrorResponse
} from '@angular/common/http';
import {ShareService} from './share.service';
import {catchError, retry} from 'rxjs/operators';
import {throwError} from 'rxjs';
import {ResponseMessage} from '../shared/responseMessage.model';

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
    return next.handle(req)
      .pipe(
        retry(1),
        catchError((error: HttpErrorResponse) => {
          let errorMessage = '';
          if (error.status === 408){
            errorMessage = error.error.message;
            window.alert(errorMessage);
          }else{
            if (error.error.message === 'Unauthorized'){
              errorMessage = 'The connection with the DB is lost, try later';
              error.error.message = errorMessage;
              window.alert(errorMessage);
            }
          }
          return throwError(error);
        })
      );
  }
}
