import { SERVER_API_URL } from '../../app.constants';
import { HttpClient } from '@angular/common/http';

export interface ServiceParams {
    apiUrl: string;
}

export class ExcApiService {

    readonly resourceUrl = SERVER_API_URL + this.params.apiUrl;

    constructor(
        readonly params: ServiceParams,
        readonly http: HttpClient) { }

}
