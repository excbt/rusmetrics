import { SpyObject } from './spyobject';
import { JhiTrackerService } from '../../../../main/webapp2/app/shared/tracker/tracker.service';

export class MockTrackerService extends SpyObject {

    constructor() {
        super(JhiTrackerService);
    }

    connect() {}
}
