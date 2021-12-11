package com.uwgb.GBCoin.API.Configs;

import com.uwgb.GBCoin.API.Repositories.UserRepository;
import com.uwgb.GBCoin.Model.User;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserConfig {

    @Bean
    CommandLineRunner commandLineRunner(
            UserRepository repository
    ) {
        return args -> {
            repository.save(
            new User("Test@gmail.com",
                    "LS0tLS1CRUdJTiBQVUJMSUMgS0VZLS0tLS0KTUlJQklqQU5CZ2txaGtpRzl3MEJBUUVGQUFPQ0FROEFNSUlCQ2dLQ0FRRUEwK1BmYnVmUGMxSEo0VkJoak94bwpTTk5mYjJoQW9zcDNuWjlvYTRJU3liazRVeGVJTzVLT1lKcTIzSUREZnJ3a0J5OFIrdGNtb1cxZFFlaGpCdUtpClRQSnc1eE1rQzQ1a001L21GRGhBV0lTWlZWVlgvMElaQ1RldkNUay9ZSzNIRkxxRUlVV1RBMFlKcE1MYWpzNXAKeHNpbWw3aU96QzRHYzRvVHJCTU5yT0lpai9VMW45T1pPQmQ0WEEzM092M1pZQnhpRk9WYUFONnp6ZmVEOWMvaQpTam9UWit2VTNQejBVK2lrR0ZRT3A1a1VIZHFLcUVEaUtVTjlkU2NZcTdzUTc5ejJrczk5OXEwWVNUV2I5aEJ2Ckx4RkZuNTNoYTNFUE1GOUwvR25BcGgzSWI4Uy9ZMHFXeGdYZHR5aUdHYmhXNUk2dDV6cElMZEdXK1Nnb2VJNzgKdVFJREFRQUIKLS0tLS1FTkQgUFVCTElDIEtFWS0tLS0tCg==",
                    "LS0tLS1CRUdJTiBSU0EgUFJJVkFURSBLRVktLS0tLQpNSUlFcEFJQkFBS0NBUUVBMCtQZmJ1ZlBjMUhKNFZCaGpPeG9TTk5mYjJoQW9zcDNuWjlvYTRJU3liazRVeGVJCk81S09ZSnEyM0lERGZyd2tCeThSK3RjbW9XMWRRZWhqQnVLaVRQSnc1eE1rQzQ1a001L21GRGhBV0lTWlZWVlgKLzBJWkNUZXZDVGsvWUszSEZMcUVJVVdUQTBZSnBNTGFqczVweHNpbWw3aU96QzRHYzRvVHJCTU5yT0lpai9VMQpuOU9aT0JkNFhBMzNPdjNaWUJ4aUZPVmFBTjZ6emZlRDljL2lTam9UWit2VTNQejBVK2lrR0ZRT3A1a1VIZHFLCnFFRGlLVU45ZFNjWXE3c1E3OXoya3M5OTlxMFlTVFdiOWhCdkx4RkZuNTNoYTNFUE1GOUwvR25BcGgzSWI4Uy8KWTBxV3hnWGR0eWlHR2JoVzVJNnQ1enBJTGRHVytTZ29lSTc4dVFJREFRQUJBb0lCQUUvSDZRVDFPMk1NVkpzRgpqUFZtdmcxTnFRMWxqNUM3OHpYaUk0cjNPdVBCWGJmTGtIdjl2cVlaR3VrRGdwaGhkcDlOMWROQTEwYkl5dWhaCis0OUlIaHdpM09ma2lSbmd6MUd3end5bDhYUmkyT2tNYlVtck5Zb0c4VnpqdFQxTnNUdll6bzlJaHdZZ2hOa0kKbFFwWUpmTzI4U01IVnAwQ1oyTnhoZDZ3ZmVHdHc2SFQ5c3Z6ZEI1UWZ2a0gyNmQ0K2hCMHlmK0hHK1dIeFBhNgphTHJyOGdKOWtsZFliMlRSK2J6QjQzcUR6bjZwM3lxSnNFZ2dOK0VtSjdCNHRoZGhFRDlvRjZqR0phbUp4MDlkCkFTQjRaMElLanRZdFBpMDU4TWJZdzdZcS9ZNldCd1ZhRW9PNllPUVNIelpFVVFNZGdWMEl6S3JrQ1JuOUFETlQKSjdXNHFoRUNnWUVBL09pWlRkNkxUSVdzejJpTEtzTlM2YXoycXlOOWlScmxVRDE3TTc5N0l2OHBGUkZKdjVvbwozNEFWSjVwOUN1cFk2UDJ0YmtTTnFrNzU4a040STNMWFhHMml0K1FzV2M1bHBnL0ErZzY0bUUvV1dGUGxYcHlCCjVvRkJUUkJJYXlNbVI5ZW1KeFhYbExseEd3MWFTTVZkTTNNd3d1ekswUVBiR2JQRldwNkFMRk1DZ1lFQTFucnIKUmhTV01MV0Z5VU1kcVZOa1g4VkFsR25jdUtnditXTVg3NHpPTTZDNDRwRWR3blc3WjdZeldBTER5bWU0U1IvVgoybTQ4UG0vSVpOS21OMzVJVjFkYlZBdG9XdVZ1a2VBYmlCT2xtK3QwQk5GQ2gyNHpuQW93V2svSHFQMGtBMGI2CjNlQXRIb2hoR21LNWlpWGFNeVRuekNZSzFWZkx6by9XOTJ4cXNVTUNnWUVBb3RMWlNESGJ1dU5nODVOWjdha2MKNjBhbzlGdUFDZElnQ24zYW9PRkpWS216K0ZWT0JxWW9aR1FndW9PU3Q0RlN4b1h6cjBkQmNZRXhHQkRwK3VDSAoySURaUHMwTzVLNmw4TGk1eEFQUVFuYzhCMWZVRndwcDgwNEtEYVJMQzNid3dWWU1LYU85VEluclVNQlMvUURiCjFURDNGcmUraDd5VXhjSmdiOHdWdHZNQ2dZQnBFMEV1VFNtalpWck0xaGV5KzhyaDdIWU4vNnNGd0VmNGlxYVEKVWxBM0ZOZGZFQzhmTktTQ1U4M2xvVDBnYlIzb25UTWMxd0ttcUFicDloVStmSnllUllyVnF0ME9JdmEvZXFhaQpUTlhHaEZ5TGU2VjNTZkw0MERrT0ZhWEFVWHdTS3ZUK1BEUitqOXZhbkdFL24vbTlNcWhvNkg1SU5NSUJTWkRNCkdJNVFsd0tCZ1FDSHh1dUg1aTAxMXhmcitKYytoU0hmV1loTXc0NDFQa3IxdG0zVHpVblU1czkzdkVsMmVFbzUKWEFUNncvVVpYY3d1RGJQNEF2REZMRUxvYVpPUndHNmQ4N09Ka3ZjRWhyN3EwekVDdHExbFVmVFRKTjZPZjg5QwpoUmM1aSt0UmxZWmNhZmw3TUh6NjQ4L21SdEpFTDZCUlB0bFlkanNlbVQ4OGZuNkdLeWJXcXc9PQotLS0tLUVORCBSU0EgUFJJVkFURSBLRVktLS0tLQo="
                    )
            );
        };
    }
}