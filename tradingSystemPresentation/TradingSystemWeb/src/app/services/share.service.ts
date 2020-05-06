import {EventEmitter, Injectable, Output} from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class ShareService {
  featureSelected = new EventEmitter<string>();

  constructor() { }
}
