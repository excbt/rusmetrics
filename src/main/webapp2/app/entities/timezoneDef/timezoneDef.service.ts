import { Injectable } from '@angular/core';
import { TimezoneDef } from './timezoneDef.model';
import { ExcAbstractService } from '../../shared-blocks/exc-tools/exc-abstract-service';
import { HttpClient } from '@angular/common/http';

@Injectable()
export class TimezoneDefService extends ExcAbstractService<TimezoneDef> {

    constructor(http: HttpClient) {
        super({apiUrl: 'api/timezoneDef/'}, http);
     }

}
