import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {StoreService} from '../../services/store.service';
import {ShareService} from '../../services/share.service';

@Component({
  selector: 'app-open-store',
  templateUrl: './open-store.component.html',
  styleUrls: ['./open-store.component.css']
})
export class OpenStoreComponent implements OnInit {
  message: string;
  @ViewChild('storeNameInput', {static: false}) storeNameInputRef: ElementRef;
  @ViewChild('descriptionInput', {static: false}) descriptionInputRef: ElementRef;
  messageColor: string;

  constructor(private storeService: StoreService, private shareService: ShareService) { }

  ngOnInit(): void {
  }

  onOpenStore() {
    console.log(`open store`);
    this.storeService.openStore(this.storeNameInputRef.nativeElement.value, this.descriptionInputRef.nativeElement.value)
      .subscribe(response => {
        if (response){
          this.shareService.featureSelected.emit('Owned-stores');
        }else{
          this.errorMessage('Cant open the store');
        }
      });
  }

  errorMessage(message: string){
    this.message = message;
    this.messageColor = 'red';
  }
  onClearDetails() {
    this.message = '';
    this.storeNameInputRef.nativeElement.value = '';
    this.descriptionInputRef.nativeElement.value = '';
  }
}
