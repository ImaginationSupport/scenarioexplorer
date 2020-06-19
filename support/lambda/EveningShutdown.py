# cron(0 0 ? * * *)

# 20:00 EDT = 12:00 UTC

import boto3

region = 'us-east-1'

instances = [
    'i-04dd904e4c2630768',  # Production
    # 'i-0303313443cbb5f42',  # Dev
    'i-0c963aa856e1be563'  # Dev 2
]


def lambda_handler(event, context):
    print('Stopping: ', instances)

    ec2 = boto3.client('ec2', region_name=region)
    ec2.stop_instances(InstanceIds=instances)

    return
