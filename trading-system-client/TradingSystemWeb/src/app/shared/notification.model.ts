export class NotificationDto {
  content: string;
  style: string;
  public dismissed = false;
  title: string;
  constructor(title: string, content: string, principal: string, style?: string) {
    this.content = content;
    if (style === undefined || style === null){
      this.style = 'info';
    }else{
      this.style = style;
    }
    this.title = title;
    if (this.title === undefined){
     this.title = 'Notification';
    }
  }
}
