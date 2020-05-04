import {Component, Input, OnInit} from '@angular/core';
import {UserSystem} from '../../../../../shared/userSystem.model';
import {UserService} from '../../../../../services/user.service';

@Component({
  selector: 'app-user-element',
  templateUrl: './user-element.component.html',
  styleUrls: ['./user-element.component.css']
})
export class UserElementComponent implements OnInit {
  @Input() user: UserSystem;
  constructor(private userService: UserService) { }

  ngOnInit(): void {
  }

  onSelected() {
    this.userService.userSelectedEvent.emit(this.user);

  }
}
