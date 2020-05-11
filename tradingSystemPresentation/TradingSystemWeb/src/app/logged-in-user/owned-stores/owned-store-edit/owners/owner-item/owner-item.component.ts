import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';

@Component({
  selector: 'app-owner-item',
  templateUrl: './owner-item.component.html',
  styleUrls: ['./owner-item.component.css']
})
export class OwnerItemComponent implements OnInit {

  @Input() username: string;
  constructor() { }

  ngOnInit(): void {
  }

}
