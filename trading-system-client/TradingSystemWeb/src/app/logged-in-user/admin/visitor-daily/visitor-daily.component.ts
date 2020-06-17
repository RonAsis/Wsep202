import {Component, ElementRef, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {UserService} from '../../../services/user.service';
import {DailyVistorDto} from '../../../shared/dailyVistor.model';
import {UpdateDailyVisitor} from '../../../shared/updateDailyVisitorDto.model';

@Component({
  selector: 'app-visitor-daily',
  templateUrl: './visitor-daily.component.html',
  styleUrls: ['./visitor-daily.component.css']
})
export class VisitorDailyComponent implements OnInit, OnDestroy {

  @ViewChild('startTime', {static: false}) startTime: ElementRef;

  @ViewChild('endTime', {static: false}) endTime: ElementRef;

  headElements = ['Date', 'Guests', 'Owner Stores', 'Manager Stores', 'Simple Users', 'admins'];

  messageColor: string;
  message: string;
  today: string;
  firstIndex: number;
  lastIndex: number;

  dailyVistors: DailyVistorDto[];
  constructor(private userService: UserService) { }

  ngOnDestroy(): void {
      this.userService.stopDailyVisitor();
    }

  ngOnInit(): void {
    this.firstIndex = 0;
    this.lastIndex = 10;
    this.today = new Date().toISOString().split('T')[0];
    this.updateDailyVistors(new Date(), new Date());
  }
  private updateDailyVistors(start: Date, end: Date){
    this.userService.getDailyVisitors(start, end, this.firstIndex, this.lastIndex)
      .subscribe(response => {
        if (response){
          this.dailyVistors = response;
          this.sucMessage('succeed get daily visitor from Server');
        }else{
          this.errorMessage('cant get daily visitor from Server');
        }
      });
    this.userService.dailyVisitorReceivedEvent.subscribe((response: UpdateDailyVisitor) => {
      if (response !== undefined){
        const today = new Date();
        const dailyVistorDto = this.dailyVistors.find(daily => daily.date === today);
        if (response.admins > 0){
          dailyVistorDto.admins = dailyVistorDto.admins ++;
        }else if (response.simpleUser > 0){
          dailyVistorDto.simpleUser = dailyVistorDto.simpleUser ++;
        }else if (response.managerStores > 0){
          dailyVistorDto.managerStores = dailyVistorDto.managerStores ++;
        }else if (response.ownerStores > 0){
          dailyVistorDto.ownerStores = dailyVistorDto.ownerStores ++;
        }else if (response.guests > 0){
          dailyVistorDto.guests = dailyVistorDto.guests ++;
        }
        console.log(dailyVistorDto);
        console.log(this.dailyVistors);
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

  getDate(date: Date) {
    return new Date(date).toISOString().split('T')[0];
  }
}
