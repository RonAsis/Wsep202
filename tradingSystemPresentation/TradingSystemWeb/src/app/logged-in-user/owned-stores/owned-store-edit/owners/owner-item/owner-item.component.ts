import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Manager} from '../../../../../shared/manager.model';

@Component({
  selector: 'app-owner-item',
  templateUrl: './owner-item.component.html',
  styleUrls: ['./owner-item.component.css']
})
export class OwnerItemComponent implements OnInit {

  @Input() manager: Manager;
  constructor() { }

  ngOnInit(): void {
  }

}
