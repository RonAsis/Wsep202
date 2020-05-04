import {Injectable} from '@angular/core';
import {WebSocketAPI} from '../shared/apis/webSocketApi.model';

@Injectable({
  providedIn: 'root'
})
export class WebSocketService {

  private webSocketAPI: WebSocketAPI;

  public setWebSocketAPI(value: WebSocketAPI) {
    if (this.webSocketAPI === undefined) {
      this.webSocketAPI = value;
    }
  }

  public getWebSocketAPI(): WebSocketAPI {
    return this.webSocketAPI;
  }

  constructor() {
  }

}
