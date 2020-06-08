import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {UserService} from '../../../services/user.service';
import {DailyVistorDto} from '../../../shared/dailyVistor.model';

@Component({
  selector: 'app-visitor-daily',
  templateUrl: './visitor-daily.component.html',
  styleUrls: ['./visitor-daily.component.css']
})
export class VisitorDailyComponent implements OnInit {

  @ViewChild('startTime', {static: false}) startTime: ElementRef;

  @ViewChild('endTime', {static: false}) endTime: ElementRef;

  headElements = ['Date', 'Guests', 'Owner Stores', 'Manager Stores', 'Simple Users', 'admins'];

  messageColor: string;
  message: string;
  today: string;

  dailyVistors: DailyVistorDto[];
  constructor(private userService: UserService) { }

  ngOnInit(): void {
    console.log('here');
    this.today = new Date().toISOString().split('T')[0];
    this.updateDailyVistors(new Date(), new Date());
  }
  private updateDailyVistors(start: Date, end: Date){
    this.userService.getDailyVisitors(start, end)
      .subscribe(response => {
        if (response){
          this.dailyVistors = response;
          this.sucMessage('succeed get daily visitor from Server');
        }else{
          this.errorMessage('cant get daily visitor from Server');
        }
      });
  }

  onGetVistorDaily() {
    this.updateDailyVistors(this.startTime.nativeElement.value, this.endTime.nativeElement.value);
  }

  errorMessage(message: string) {
    this.message = message;
    this.messageColor = 'red';
  }

  sucMessage(message: string) {
    this.message = message;
    this.messageColor = 'blue';
  }
}
